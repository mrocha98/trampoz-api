package br.gov.sp.fatec.trampoz_api.filters;

import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.shared.HttpMethodsEnum;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import br.gov.sp.fatec.trampoz_api.users.UserService;
import lombok.Builder;
import lombok.Getter;
import org.javatuples.Pair;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuthFilter implements Filter {

    @Builder
    @Getter
    private static class PermissionsByHttpMethod {
        public final Set<RolesEnum> GET;
        public final Set<RolesEnum> POST;
        public final Set<RolesEnum> PUT;
        public final Set<RolesEnum> DELETE;
    }

    private ServletContext context;
    private UserService userService;
    private RolesService rolesService;
    private final AbstractMap<String, PermissionsByHttpMethod> permissionsByUrls = new HashMap<>();
    private List<String> whiteListUrls;

    private void initPermissionsByUrls() {
        this.permissionsByUrls.put("/ping",
                PermissionsByHttpMethod.builder()
                        .GET(new HashSet<>())
                        .build());
        this.permissionsByUrls.put("/freelancers",
                PermissionsByHttpMethod.builder()
                        .GET(new HashSet<>(Arrays.asList(RolesEnum.ADMIN, RolesEnum.FREELANCER, RolesEnum.CONTRACTOR)))
                        .POST(new HashSet<>())
                        .build());
        this.permissionsByUrls.put("/contractors",
                PermissionsByHttpMethod.builder()
                        .GET(new HashSet<>())
                        .POST(new HashSet<>())
                        .build());
        this.permissionsByUrls.put("/jobs",
                PermissionsByHttpMethod.builder()
                        .GET(new HashSet<>(Arrays.asList(RolesEnum.ADMIN, RolesEnum.FREELANCER, RolesEnum.CONTRACTOR)))
                        .POST(new HashSet<>(Arrays.asList(RolesEnum.ADMIN, RolesEnum.CONTRACTOR)))
                        .PUT(new HashSet<>(Arrays.asList(RolesEnum.ADMIN, RolesEnum.CONTRACTOR)))
                        .DELETE(new HashSet<>(Arrays.asList(RolesEnum.ADMIN)))
                        .build());
    }

    private void initWhiteListUrls() {
        this.whiteListUrls = new ArrayList<>(Arrays.asList(
                "/freelancers",
                "/contractors"
        ));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        this.userService = new UserService();
        this.rolesService = new RolesService();

        this.initWhiteListUrls();
        this.initPermissionsByUrls();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean IS_URL_IN_WHITELIST_URLS = this.whiteListUrls.contains(req.getServletPath());
        boolean IS_POST = req.getMethod().equals(HttpMethodsEnum.POST.getValue());
        if (IS_POST && IS_URL_IN_WHITELIST_URLS) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null) {
            this.unauthorized(res, "No authentication token provided");
            return;
        }

        Pair<String, String> userCredentials = extractUserCredentialsFromAuthHeader(authHeader);
        String email = userCredentials.getValue0();
        String password = userCredentials.getValue1();

        boolean HAS_SOME_CREDENTIAL_EMPTY = email.isEmpty() || password.isEmpty();
        if (HAS_SOME_CREDENTIAL_EMPTY) {
            this.unauthorized(res, "Invalid authentication token");
            return;
        }

        UserEntity user = extractUserFromDatabase(email);
        if (user == null) {
            this.unauthorized(res, "Bad credentials");
            return;
        }

        boolean INVALID_CREDENTIALS = !user.checkIfCredentialsAreValid(email, password);
        if (INVALID_CREDENTIALS) {
            this.unauthorized(res, "Bad credentials");
            return;
        }

        String method = req.getMethod();
        String url = req.getRequestURI();

        Set<RolesEnum> acceptedRolesEnums = getAcceptedRolesEnums(url, method);

        boolean METHOD_NOT_IMPLEMENTED_IN_URL = acceptedRolesEnums == null;
        if (METHOD_NOT_IMPLEMENTED_IN_URL) {
            this.notFound(res, "Method not found in " + url);
            return;
        }

        boolean NO_ROLE_RESTRICTION = acceptedRolesEnums.isEmpty();
        if (NO_ROLE_RESTRICTION) {
            chain.doFilter(req, res);
            return;
        }

        Set<RoleEntity> acceptedRolesEntities = extractRolesEntitiesFromEnums(acceptedRolesEnums);
        Set<RoleEntity> userRolesEntities = user.getRoles();

        for (RoleEntity acceptedRole : acceptedRolesEntities) {
            for (RoleEntity userRole : userRolesEntities) {
                UUID acceptedRoleId = acceptedRole.getId();
                UUID userRoleId = userRole.getId();

                boolean USER_HAS_PERMISSION = acceptedRoleId.equals(userRoleId);
                if (USER_HAS_PERMISSION) {
                    chain.doFilter(req, res);
                    return;
                }
            }
        }

        this.forbidden(res, "Insufficient permissions");
    }

    private PermissionsByHttpMethod getPermissionsFromUrl(String url) {
        return this.permissionsByUrls.get(url);
    }

    private Set<RolesEnum> getAcceptedRolesEnums(String url, String method) {
        PermissionsByHttpMethod permissions = getPermissionsFromUrl(url);
        if (permissions == null) return null;

        Set<RolesEnum> rolesEnums;

        if (method.equals(HttpMethodsEnum.GET.getValue())) rolesEnums = permissions.getGET();
        else if (method.equals(HttpMethodsEnum.POST.getValue())) rolesEnums = permissions.getPOST();
        else if (method.equals(HttpMethodsEnum.PUT.getValue())) rolesEnums = permissions.getPUT();
        else if (method.equals(HttpMethodsEnum.DELETE.getValue())) rolesEnums = permissions.getDELETE();
        else rolesEnums = null;

        return rolesEnums;
    }

    private Pair<String, String> extractUserCredentialsFromAuthHeader(String authHeader) {
        Pair<String, String> emptyCredentials = new Pair<>("", "");

        StringTokenizer stringTokenizer = new StringTokenizer(authHeader);
        if (!stringTokenizer.hasMoreTokens()) return emptyCredentials;

        String basic = stringTokenizer.nextToken();
        if (!basic.equalsIgnoreCase("Basic")) return emptyCredentials;

        try {
            String credentials = new String(Base64.getDecoder().decode(stringTokenizer.nextToken()));
            int separatorPosition = credentials.indexOf(":");
            if (separatorPosition == -1) return emptyCredentials;

            String _email = credentials.substring(0, separatorPosition).trim();
            String _password = credentials.substring(separatorPosition + 1).trim();

            return new Pair<>(_email, _password);
        } catch (Exception e) {
            e.printStackTrace();
            return emptyCredentials;
        }
    }

    private UserEntity extractUserFromDatabase(String email) {
        return userService.findByEmail(email);
    }

    private RoleEntity extractRoleFromDatabase(RolesEnum role) {
        RoleEntity foundRole = rolesService.findByName(role);
        return foundRole;
    }

    private Set<RoleEntity> extractRolesEntitiesFromEnums(Set<RolesEnum> rolesEnums) {
        return rolesEnums.stream().map(this::extractRoleFromDatabase).collect(Collectors.toSet());
    }

    private void unauthorized(HttpServletResponse res, String message) throws IOException {
        res.setHeader("WWW-Authenticate", "Basic realm=\"" + "PROTECTED" + "\"");
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    private void forbidden(HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_FORBIDDEN, message);
    }

    private void notFound(HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }

    @Override
    public void destroy() {
    }

}
