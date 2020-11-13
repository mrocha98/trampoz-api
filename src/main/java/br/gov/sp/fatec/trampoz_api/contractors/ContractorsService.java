package br.gov.sp.fatec.trampoz_api.contractors;

import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.shared.GenericService;

import javax.persistence.PersistenceException;

public class ContractorsService extends GenericService implements ContractorsDao {

    @Override
    public void create(ContractorEntity contractor) {
        RoleEntity contractorRole = new RolesService().findByName(RolesEnum.CONTRACTOR);
        contractor.addRole(contractorRole);

        entityManager.persist(contractor);
    }

    @Override
    public void createAndCommit(ContractorEntity contractor) {
        try {
            entityManager.getTransaction().begin();
            create(contractor);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void update(ContractorEntity contractor) {
        if (contractor.getId() == null) {
            create(contractor);
            return;
        }

        entityManager.merge(contractor);
    }

    @Override
    public void updateAndCommit(ContractorEntity contractor) {
        try {
            entityManager.getTransaction().begin();
            update(contractor);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }
}
