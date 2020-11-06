package br.gov.sp.fatec.trampoz_api.users;

import java.util.UUID;

public interface UserDao {
    public UserEntity findById(UUID id);

    public Boolean checkIfEmailAlreadyInUse(String email);

    public void delete(UUID id);

    public void deleteAndCommit(UUID id);
}
