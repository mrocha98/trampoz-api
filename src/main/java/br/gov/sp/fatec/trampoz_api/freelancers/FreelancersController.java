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
        res.getWriter().print("freelancers");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FreelancerEntity freelancer = objectMapper.readValue(req.getReader(), FreelancerEntity.class);
        PrintWriter out = resp.getWriter();

        try {
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
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            out.flush();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
