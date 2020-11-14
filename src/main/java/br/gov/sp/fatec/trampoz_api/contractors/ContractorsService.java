package br.gov.sp.fatec.trampoz_api.contractors;

import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.users.UserService;

import javax.persistence.PersistenceException;

public class ContractorsService extends UserService implements ContractorsDao {

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

}
