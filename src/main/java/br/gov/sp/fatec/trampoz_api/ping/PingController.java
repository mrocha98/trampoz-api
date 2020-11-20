package br.gov.sp.fatec.trampoz_api.ping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class PingController extends HttpServlet {
    private static final Long serialVersionUID = 2401764599755134781L;
    private final ObjectMapper objectMapper;

    public PingController() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        ObjectNode responseJson = objectMapper.createObjectNode().put("ping", "pong");
        out.print(responseJson);
        res.setStatus(HttpServletResponse.SC_OK);

        out.flush();
    }
}
