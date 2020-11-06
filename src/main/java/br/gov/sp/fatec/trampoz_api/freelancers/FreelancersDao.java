package br.gov.sp.fatec.trampoz_api.freelancers;

import java.util.List;
import java.util.UUID;

public interface FreelancersDao {

    public void create(FreelancerEntity freelancer);

    public void createAndCommit(FreelancerEntity freelancer);

    public void update(FreelancerEntity freelancer);

    public void updateAndCommit(FreelancerEntity freelancer);
}
