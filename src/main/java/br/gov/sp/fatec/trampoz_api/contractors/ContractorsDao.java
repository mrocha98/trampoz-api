package br.gov.sp.fatec.trampoz_api.contractors;

import java.util.UUID;

public interface ContractorsDao {

    ContractorEntity findById(UUID id);

    void create(ContractorEntity contractor);

    void createAndCommit(ContractorEntity contractor);

}
