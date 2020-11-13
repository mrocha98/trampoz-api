package br.gov.sp.fatec.trampoz_api.filters;

import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import br.gov.sp.fatec.trampoz_api.users.UserService;
import org.javatuples.Pair;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class AuthFilter implements Filter {

    private ServletContext context;
    private UserService userService;
    private List<String> whiteListUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.context = filterConfig.getServletContext();
        this.userService = new UserService();
        this.whiteListUrls = new ArrayList<>(Arrays.asList(
                "/freelancers",
                "/contractors"
        ));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Boolean isUrlInWhiteList = this.whiteListUrls.contains(req.getServletPath());
        Boolean isPost = req.getMethod().equals("POST");

        if (isPost && isUrlInWhiteList) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null) {
            this.unauthorized(res, "No authentication token provided");
            return;
        }

        Pair<String, String> userCredentials = extractUserCredentials(authHeader);
        String email = userCredentials.getValue0();
        String password = userCredentials.getValue1();

        if (email.isEmpty() || password.isEmpty()) {
            this.unauthorized(res, "Invalid authentication token");
            return;
        }

        UserEntity user = userService.findByEmail(email);
        if (user == null) {
            this.unauthorized(res, "Bad credentials");
            return;
        }

        Boolean credentialsAreValid = user.checkIfCredentialsAreValid(email, password);

        if (!credentialsAreValid) {
            this.unauthorized(res, "Bad credentials");
            return;
        }

        chain.doFilter(req, res);
    }

    private Pair<String, String> extractUserCredentials(String authHeader) {
        Pair<String, String> emptyCredentials = new Pair<>("", "");

        StringTokenizer stringTokenizer = new StringTokenizer(authHeader);
        if (!stringTokenizer.hasMoreTokens()) return emptyCredentials;

        String basic = stringTokenizer.nextToken();
        if (!basic.equalsIgnoreCase("Basic")) return emptyCredentials;;

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

    private void unauthorized(HttpServletResponse res, String message) throws IOException {
        res.setHeader("WWW-Authenticate", "Basic realm=\"" + "PROTECTED" + "\"");
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }

    @Override
    public void destroy() {
    }

}
