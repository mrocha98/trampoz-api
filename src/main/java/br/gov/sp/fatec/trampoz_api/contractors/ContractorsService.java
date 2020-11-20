package br.gov.sp.fatec.trampoz_api.contractors;

import br.gov.sp.fatec.trampoz_api.helpers.PaginationHelper;
import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.users.UserService;

import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

public class ContractorsService extends UserService implements ContractorsDao {

    @Override
    public ContractorEntity findById(UUID id) {
        return entityManager.find(ContractorEntity.class, id);
    }

    @Override
    public List<ContractorEntity> findAll(Integer page, Integer limit) {
        String jpql = "select c from ContractorEntity c order by name desc";
        TypedQuery<ContractorEntity> typedQuery = entityManager.createQuery(jpql, ContractorEntity.class);

        PaginationHelper paginationHelper = new PaginationHelper(page, limit);

        int firstResult = paginationHelper.getOffset();
        typedQuery.setFirstResult(firstResult);
        typedQuery.setMaxResults(limit);

        return typedQuery.getResultList();
    }

    @Override
    public Long getTotal() {
        Query queryTotal = entityManager.createQuery("select count(c.id) from ContractorEntity c");
        Long total = (Long) queryTotal.getSingleResult();
        return total;
    }

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
