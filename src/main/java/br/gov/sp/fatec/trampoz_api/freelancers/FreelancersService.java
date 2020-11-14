package br.gov.sp.fatec.trampoz_api.freelancers;

import br.gov.sp.fatec.trampoz_api.roles.RoleEntity;
import br.gov.sp.fatec.trampoz_api.roles.RolesEnum;
import br.gov.sp.fatec.trampoz_api.roles.RolesService;
import br.gov.sp.fatec.trampoz_api.users.UserService;

import javax.persistence.PersistenceException;
import java.util.UUID;

public class FreelancersService extends UserService implements FreelancersDao {

    @Override
    public FreelancerEntity findById(UUID id) {
        return entityManager.find(FreelancerEntity.class, id);
    }

    @Override
    public void create(FreelancerEntity freelancer) {
        RoleEntity freelancerRole = new RolesService().findByName(RolesEnum.FREELANCER);
        freelancer.addRole(freelancerRole);

        this.entityManager.persist(freelancer);
    }

    @Override
    public void createAndCommit(FreelancerEntity freelancer) {
        try {
            entityManager.getTransaction().begin();
            create(freelancer);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void update(FreelancerEntity freelancer) {
        if (freelancer.getId() == null) {
            create(freelancer);
            return;
        }

        entityManager.merge(freelancer);
    }

    @Override
    public void updateAndCommit(FreelancerEntity freelancer) {
        try {
            entityManager.getTransaction().begin();
            update(freelancer);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }
}
