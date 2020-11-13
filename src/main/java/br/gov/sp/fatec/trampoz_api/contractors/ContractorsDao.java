package br.gov.sp.fatec.trampoz_api.contractors;

public interface ContractorsDao {

    void create(ContractorEntity contractor);

    void createAndCommit(ContractorEntity contractor);

    void update(ContractorEntity contractor);

    void updateAndCommit(ContractorEntity contractor);
}
