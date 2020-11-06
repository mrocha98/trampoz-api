package br.gov.sp.fatec.trampoz_api.roles;

public interface RolesDao {
    public RoleEntity findByName(RolesEnum name);
}
