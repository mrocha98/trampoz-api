package br.gov.sp.fatec.trampoz_api.freelancers;

import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class FreelancersController extends HttpServlet {

    private static final Long serialVersionUID = 4787108556148621714l;
    private ObjectMapper objectMapper;
    private FreelancersService freelancersService;

    public FreelancersController() {
        objectMapper = new ObjectMapper();
        freelancersService = new FreelancersService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        UUID id = UUID.fromString(req.getParameter("id"));
        FreelancerEntity freelancer = freelancersService.findById(id);

        if (freelancer != null) {
            String responseJson = objectMapper.writeValueAsString(freelancer);
            out.print(responseJson);
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            ObjectNode freelancerJson = objectMapper.createObjectNode().put("error", "user not found");
            out.print(freelancerJson);
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        FreelancerEntity freelancer = objectMapper.readValue(req.getReader(), FreelancerEntity.class);

        Boolean emailAlreadyInUse = freelancersService.checkIfEmailAlreadyInUse(freelancer.getEmail());
        if (emailAlreadyInUse) {
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Email already in use");
            out.print(responseJson);

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            freelancersService.createAndCommit(freelancer);
            String freelancerJson = objectMapper.writeValueAsString(freelancer);
            out.print(freelancerJson);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }

        out.flush();
    }
}
