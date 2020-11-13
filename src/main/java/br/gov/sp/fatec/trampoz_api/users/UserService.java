package br.gov.sp.fatec.trampoz_api.users;

import br.gov.sp.fatec.trampoz_api.freelancers.FreelancerEntity;
import br.gov.sp.fatec.trampoz_api.shared.GenericService;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.UUID;

public class UserService extends GenericService implements UserDao {

    @Override
    public UserEntity findById(UUID id) {
        return entityManager.find(UserEntity.class, id);
    }

    @Override
    public UserEntity findByEmail(String email) {
        String jpql = "select u from UserEntity u where u.email = :email";

        TypedQuery<UserEntity> typedQuery = entityManager.createQuery(jpql, UserEntity.class);
        typedQuery.setParameter("email", email);

        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException exception) {
            return null;
        }
    }

    @Override
    public Boolean checkIfEmailAlreadyInUse(String email) {
        String jpql = "select count(id) from UserEntity u where u.email = :email";

        TypedQuery<Long> typedQuery = entityManager.createQuery(jpql, Long.class);
        typedQuery.setParameter("email", email);

        return (Long) typedQuery.getSingleResult() > 0;
    }

    @Override
    public void delete(UUID id) {
        UserEntity foundUser = this.findById(id);

        if (foundUser == null) {
            throw new RuntimeException("No users were found");
        }

        entityManager.remove(foundUser);
    }

    @Override
    public void deleteAndCommit(UUID id) {
        try {
            entityManager.getTransaction().begin();
            delete(id);
            entityManager.getTransaction().commit();
        } catch (PersistenceException error) {
            error.printStackTrace();
            entityManager.getTransaction().rollback();
        }
    }
}
