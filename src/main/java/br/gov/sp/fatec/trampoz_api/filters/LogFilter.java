package br.gov.sp.fatec.trampoz_api.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogFilter implements Filter {
    private ServletContext context;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.context = config.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        Long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(req, res);
        } finally {
            Long elapsedTime = System.currentTimeMillis() - startTime;

            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            // GET /lorem 200 100ms
            List<String> contentsToLog = new ArrayList(Arrays.asList(
                    request.getMethod(),
                    request.getRequestURI(),
                    String.valueOf(response.getStatus()),
                    elapsedTime.toString().concat("ms")
            ));

            StringBuilder logMessage = new StringBuilder();

            contentsToLog.forEach(content -> logMessage.append(content).append(' '));

            this.context.log(logMessage.toString());
        }
    }

    @Override
    public void destroy() {
    }
}
