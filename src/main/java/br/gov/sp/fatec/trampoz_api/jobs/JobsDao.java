package br.gov.sp.fatec.trampoz_api.jobs;

import br.gov.sp.fatec.trampoz_api.contractors.ContractorEntity;

import java.util.List;
import java.util.UUID;

public interface JobsDao {

    JobEntity findById(UUID id);

    List<JobEntity> findAll(Integer page, Integer limit);

    Long getTotal();

    void create(JobEntity job, ContractorEntity contractor);

    void createAndCommit(JobEntity job, ContractorEntity contractor);

    void update(JobEntity job);

    void updateAndCommit(JobEntity job);

    void delete(UUID id);

    void deleteAndCommit(UUID id);
}
