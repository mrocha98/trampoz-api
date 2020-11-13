package br.gov.sp.fatec.trampoz_api.freelancers;

import br.gov.sp.fatec.trampoz_api.users.UserEntity;

import java.util.UUID;

public interface FreelancersDao {

    FreelancerEntity findById(UUID id);

    void create(FreelancerEntity freelancer);

    void createAndCommit(FreelancerEntity freelancer);

    void update(FreelancerEntity freelancer);

    void updateAndCommit(FreelancerEntity freelancer);
}
