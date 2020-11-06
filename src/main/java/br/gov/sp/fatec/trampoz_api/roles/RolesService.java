package br.gov.sp.fatec.trampoz_api.roles;

import br.gov.sp.fatec.trampoz_api.shared.GenericService;

import javax.persistence.TypedQuery;

public class RolesService extends GenericService implements RolesDao {
    @Override
    public RoleEntity findByName(RolesEnum name) {
        String jpql = "select r from RoleEntity r where name = :name";

        TypedQuery<RoleEntity> typedQuery = entityManager.createQuery(jpql, RoleEntity.class);
        typedQuery.setParameter("name", name);

        return typedQuery.getSingleResult();
    }
}
