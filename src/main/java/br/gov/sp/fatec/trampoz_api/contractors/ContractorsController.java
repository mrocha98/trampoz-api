package br.gov.sp.fatec.trampoz_api.contractors;

import br.gov.sp.fatec.trampoz_api.utils.LocationHeaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ContractorsController extends HttpServlet {
    private static final Long serialVersionUID = 3622553053562085304L;
    private final ObjectMapper objectMapper;
    private final ContractorsService contractorsService;

    public ContractorsController() {
        this.objectMapper = new ObjectMapper();
        this.contractorsService = new ContractorsService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        ContractorEntity contractor = objectMapper.readValue(req.getReader(), ContractorEntity.class);

        boolean EMAIL_ALREADY_IN_USE = contractorsService.checkIfEmailAlreadyInUse(contractor.getEmail());
        if (EMAIL_ALREADY_IN_USE) {
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Email already in use");
            out.print(responseJson);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            contractorsService.createAndCommit(contractor);
            String freelancerJson = objectMapper.writeValueAsString(contractor);
            out.print(freelancerJson);

            res.setStatus(HttpServletResponse.SC_CREATED);
            LocationHeaderUtils.create(res, "/contractors", contractor.getId());
        }

        out.flush();
    }
}
