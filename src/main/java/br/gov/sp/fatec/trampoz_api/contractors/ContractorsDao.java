package br.gov.sp.fatec.trampoz_api.contractors;

import java.util.List;
import java.util.UUID;

public interface ContractorsDao {

    ContractorEntity findById(UUID id);

    List<ContractorEntity> findAll(Integer page, Integer limit);

    Long getTotal();

    void create(ContractorEntity contractor);

    void createAndCommit(ContractorEntity contractor);

}
