package br.gov.sp.fatec.trampoz_api.jobs;

import br.gov.sp.fatec.trampoz_api.contractors.ContractorEntity;
import br.gov.sp.fatec.trampoz_api.contractors.ContractorsService;
import br.gov.sp.fatec.trampoz_api.helpers.PaginationHelper;
import br.gov.sp.fatec.trampoz_api.utils.LocationHeaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobsController extends HttpServlet {
    private static final Long serialVersionUID = 7326358630720607420L;
    private final ObjectMapper objectMapper;
    private final JobsService jobsService;
    private final ContractorsService contractorsService;

    public JobsController() {
        this.objectMapper = new ObjectMapper();
        this.jobsService = new JobsService();
        this.contractorsService = new ContractorsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        boolean HAS_ID_PARAM = idParam != null;
        if (HAS_ID_PARAM) getSingle(req, res);
        else getAll(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        PrintWriter out = res.getWriter();

        JobEntity job = objectMapper.readValue(req.getReader(), JobEntity.class);
        UUID contractorIdFromJson;
        try {
            contractorIdFromJson = UUID.fromString(job.getContractorId());
        } catch (IllegalArgumentException exception) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Invalid contractorId");
            out.print(responseJson);
            out.flush();
            return;
        }

        ContractorEntity contractor = contractorsService.findById(contractorIdFromJson);
        if (contractor == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Contractor don't exists");
            out.print(responseJson);
            out.flush();
            return;
        }

        jobsService.createAndCommit(job, contractor);

        String jobJson = objectMapper.writeValueAsString(job);
        out.print(jobJson);

        res.setStatus(HttpServletResponse.SC_CREATED);

        LocationHeaderUtils.create(res, "/jobs", job.getId());

        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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

        UUID jobIdFromHeader;
        try {
            jobIdFromHeader = UUID.fromString(idParam);
        } catch (IllegalArgumentException exception) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Invalid id");
            out.print(responseJson);
            out.flush();
            return;
        }

        JobEntity job = jobsService.findById(jobIdFromHeader);
        if (job == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Job don't exists");
            out.print(responseJson);
            out.flush();
            return;
        }

        JobEntity jobFromJson = objectMapper.readValue(req.getReader(), JobEntity.class);

        String contractorIdFromBody = job.getContractorId();
        if (contractorIdFromBody == null) {
            updateJobOnly(res, job, jobFromJson);
            out.flush();
            return;
        }

        UUID contractorId;
        try {
            contractorId = UUID.fromString(contractorIdFromBody);
        } catch (IllegalArgumentException exception) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Invalid contractorId");
            out.print(responseJson);
            out.flush();
            return;
        }

        ContractorEntity contractor = contractorsService.findById(contractorId);
        if (contractor == null) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Contractor don't exists");
            out.print(responseJson);
            out.flush();
            return;
        }

        updateJobAndContractor(res, job, jobFromJson, contractor);
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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

        UUID jobIdFromHeader;
        try {
            jobIdFromHeader = UUID.fromString(idParam);
        } catch (IllegalArgumentException exception) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Invalid id");
            out.print(responseJson);
            out.flush();
            return;
        }

        try {
            jobsService.deleteAndCommit(jobIdFromHeader);
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            out.flush();
        } catch (RuntimeException runtimeException) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", runtimeException.getMessage());
            out.print(responseJson);
            out.flush();
        }
    }


    private void getAll(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();

        String pageParam = req.getParameter("page");
        Integer page = pageParam != null ? Math.abs(Integer.parseInt(pageParam)) : 1;
        if (page == 0) page++;

        String limitParam = req.getParameter("limit");
        Integer limit = limitParam != null ? Math.abs(Integer.parseInt(limitParam)) : 10;

        Long total = jobsService.getTotal();
        List<JobEntity> jobs = new ArrayList<>(jobsService.findAll(page, limit));

        PaginationHelper paginationHelper = new PaginationHelper(page, limit, total);
        ObjectNode responseJson = paginationHelper
                .createJsonWithPaginationInformation()
                .put("total", total)
                .putPOJO("jobs", jobs);

        res.setStatus(HttpServletResponse.SC_OK);
        out.print(responseJson);
        out.flush();
    }

    private void getSingle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        UUID id;
        try {
            id = UUID.fromString(req.getParameter("id"));
        } catch (IllegalArgumentException illegalArgumentException) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ObjectNode responseJson = objectMapper.createObjectNode().put("error", "Invalid id");
            out.print(responseJson);
            out.flush();
            return;
        }

        JobEntity job = jobsService.findById(id);
        if (job == null) {
            ObjectNode jobJson = objectMapper.createObjectNode().put("error", "job not found");
            out.print(jobJson);
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String responseJson = objectMapper.writeValueAsString(job);
            out.print(responseJson);
            res.setStatus(HttpServletResponse.SC_OK);
        }
        out.flush();
    }

    private void updateJob(HttpServletResponse res, JobEntity job, JobEntity jobFromJson, ContractorEntity contractor) throws IOException {
        if (contractor != null) job.setContractor(contractor);
        job.setTitle(jobFromJson.getTitle());
        job.setDescription(jobFromJson.getDescription());
        job.setCity(jobFromJson.getCity());
        job.setPublishingDate(jobFromJson.getPublishingDate());
        job.setState(jobFromJson.getState());
        job.setIsOpen(jobFromJson.getIsOpen());
        job.setIsRemote(jobFromJson.getIsRemote());

        jobsService.updateAndCommit(job);

        res.setStatus(HttpServletResponse.SC_OK);
        String jobJson = objectMapper.writeValueAsString(job);

        PrintWriter out = res.getWriter();
        out.print(jobJson);
        out.flush();
    }

    private void updateJobOnly(HttpServletResponse res, JobEntity job, JobEntity jobFromJson) throws IOException {
        updateJob(res, job, jobFromJson, null);
    }

    private void updateJobAndContractor(HttpServletResponse res, JobEntity job, JobEntity jobFromJson, ContractorEntity contractor) throws IOException {
        updateJob(res, job, jobFromJson, contractor);
    }
}
