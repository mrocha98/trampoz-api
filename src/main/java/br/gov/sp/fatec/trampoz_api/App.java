package br.gov.sp.fatec.trampoz_api;

import br.gov.sp.fatec.trampoz_api.freelancers.FreelancerEntity;
import br.gov.sp.fatec.trampoz_api.freelancers.FreelancersService;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;

import java.math.BigDecimal;
import java.sql.Date;

public class App {
    public static void main(String[] args) {

        FreelancerEntity freelancer = new FreelancerEntity("Juquinha", "juquinha@email.com", "qwerty123", new Date(System.currentTimeMillis()), 'M');
        freelancer.setBio("lorem ipsum");
        freelancer.setAvatarLink("https://thumbs.dreamstime.com/b/portrait-handsome-middle-age-man-happy-17899558.jpg");
        freelancer.setPricePerHour(new BigDecimal(25.49));

        FreelancersService freelancersService = new FreelancersService();

        freelancersService.createAndCommit(freelancer);
        freelancersService.deleteAndCommit(freelancer.getId());
    }
}
