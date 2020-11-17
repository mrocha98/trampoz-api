package br.gov.sp.fatec.trampoz_api.freelancers;

import br.gov.sp.fatec.trampoz_api.utils.LocationHeaderUtils;
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

    private static final Long serialVersionUID = 4787108556148621714L;
    private final ObjectMapper objectMapper;
    private final FreelancersService freelancersService;

    public FreelancersController() {
        objectMapper = new ObjectMapper();
        freelancersService = new FreelancersService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        String idParam = req.getParameter("id");
        if (idParam == null) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Missing id");
            out.print(responseJson);
            out.flush();
            return;
        }
        UUID id = UUID.fromString(idParam);
        FreelancerEntity freelancer = freelancersService.findById(id);

        if (freelancer == null) {
            ObjectNode freelancerJson = objectMapper.createObjectNode().put("error", "user not found");
            out.print(freelancerJson);
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String responseJson = objectMapper.writeValueAsString(freelancer);
            out.print(responseJson);
            res.setStatus(HttpServletResponse.SC_OK);
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        FreelancerEntity freelancer = objectMapper.readValue(req.getReader(), FreelancerEntity.class);

        boolean EMAIL_ALREADY_IN_USE = freelancersService.checkIfEmailAlreadyInUse(freelancer.getEmail());
        if (EMAIL_ALREADY_IN_USE) {
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Email already in use");
            out.print(responseJson);

            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            freelancersService.createAndCommit(freelancer);
            String freelancerJson = objectMapper.writeValueAsString(freelancer);
            out.print(freelancerJson);

            res.setStatus(HttpServletResponse.SC_CREATED);
            LocationHeaderUtils.create(res, "/freelancers", freelancer.getId());
        }

        out.flush();
    }
}
