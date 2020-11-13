package br.gov.sp.fatec.trampoz_api.filters;

import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.shared.HttpMethodsEnum;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import br.gov.sp.fatec.trampoz_api.users.UserService;
import lombok.Builder;
import lombok.Getter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RolesPermissionsFilter implements Filter {

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

    private void initPermissionsByUrls() {
        this.permissionsByUrls.put("/freelancers",
                PermissionsByHttpMethod.builder()
                        .GET(new HashSet<>(Arrays.asList(RolesEnum.ADMIN, RolesEnum.FREELANCER)))
                        .POST(new HashSet<>())
                        .build());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        this.userService = new UserService();
        this.rolesService = new RolesService();
        this.initPermissionsByUrls();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String method = req.getMethod();
        String url = req.getRequestURI();

        PermissionsByHttpMethod permissionsByMethod = this.permissionsByUrls.get(url);
        boolean URL_NOT_FOUND = permissionsByMethod == null;
        if (URL_NOT_FOUND) {
            this.notFound(res, "Url not found");
            return;
        }

        Set<RolesEnum> acceptedRolesEnums;
        if (method.equals(HttpMethodsEnum.GET.getValue())) acceptedRolesEnums = permissionsByMethod.getGET();
        else if (method.equals(HttpMethodsEnum.POST.getValue())) acceptedRolesEnums = permissionsByMethod.getPOST();
        else if (method.equals(HttpMethodsEnum.PUT.getValue())) acceptedRolesEnums = permissionsByMethod.getPUT();
        else if (method.equals(HttpMethodsEnum.DELETE.getValue())) acceptedRolesEnums = permissionsByMethod.getDELETE();
        else {
            this.notAcceptable(res);
            return;
        }

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

        String userIdHeader = req.getHeader("userId");
        UUID idFromHeader;

        try {
            idFromHeader = UUID.fromString(userIdHeader);
        } catch (NullPointerException nullPointerException) {
            badRequest(res, "Missing userId header");
            return;
        } catch (IllegalArgumentException invalidIdException) {
            invalidIdException.printStackTrace();
            this.badRequest(res, "Invalid id format or value");
            return;
        }

        UserEntity user = this.extractUser(idFromHeader);

        boolean NOT_FOUND_USER = user == null;
        if (NOT_FOUND_USER) {
            this.notFound(res, "User not found");
            return;
        }

        Set<RoleEntity> acceptedRolesEntities = extractRolesFromEnums(acceptedRolesEnums);
        Set<RoleEntity> userRolesEntities = user.getRoles();

        for (RoleEntity acceptedRole : acceptedRolesEntities) {
            for (RoleEntity userRole : userRolesEntities) {
                UUID acceptedRoleId = acceptedRole.getId();
                UUID userRoleId = userRole.getId();
                boolean USER_HAS_PERMISSION = acceptedRoleId.equals(userRoleId);
                System.out.println(acceptedRoleId + " | " + userRoleId + " | " + USER_HAS_PERMISSION);
                if (USER_HAS_PERMISSION) {
                    chain.doFilter(req, res);
                    return;
                }
            }
        }

        this.forbidden(res, "Insufficient permissions");
    }

    private UserEntity extractUser(UUID id) {
        UserEntity user = userService.findById(id);
        return user;
    }

    private RoleEntity extractRole(RolesEnum role) {
        RoleEntity foundRole = rolesService.findByName(role);
        return foundRole;
    }

    private Set<RoleEntity> extractRolesFromEnums(Set<RolesEnum> rolesEnums) {
        return rolesEnums.stream().map(this::extractRole).collect(Collectors.toSet());
    }

    private void notFound(HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_NOT_FOUND, message);
    }

    private void badRequest(HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
    }

    private void forbidden(HttpServletResponse res, String message) throws IOException {
        res.sendError(HttpServletResponse.SC_FORBIDDEN, message);
    }

    private void notAcceptable(HttpServletResponse res) throws IOException {
        res.setHeader("Allow", "GET, POST, PUT, DELETE");
        res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
    }

    @Override
    public void destroy() {
    }
}
