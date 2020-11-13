package br.gov.sp.fatec.trampoz_api.users;

import java.util.UUID;

public interface UserDao {
    UserEntity findById(UUID id);

    UserEntity findByEmail(String email);

    Boolean checkIfEmailAlreadyInUse(String email);

    void delete(UUID id);

    void deleteAndCommit(UUID id);
}
