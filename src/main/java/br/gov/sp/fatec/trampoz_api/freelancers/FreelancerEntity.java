package br.gov.sp.fatec.trampoz_api.freelancers;

import br.gov.sp.fatec.trampoz_api.resumes.ResumeEntity;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.dialect.PostgreSQL95Dialect;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fre_freelancer")
@PrimaryKeyJoinColumn(name = "fre_id")
public class FreelancerEntity extends UserEntity {

    public FreelancerEntity(String name, String email, String password, Date birthday, Character gender) {
        super(name, email, password, birthday, gender);
    }

    public FreelancerEntity(String name, String email, String password, Date birthday, Character gender, String bio, String avatarLink, BigDecimal pricePerHour) {
        super(name, email, password, birthday, gender);
        setBio(bio);
        setAvatarLink(avatarLink);
        setPricePerHour(pricePerHour);
    }

    @Column(name = "fre_bio", columnDefinition = "text")
    private String bio;

    @Column(name = "fre_avatar_link", columnDefinition = "text")
    private String avatarLink;

    @Column(name = "fre_price_per_hour", columnDefinition = "decimal", precision = 5, scale = 2)
    private BigDecimal pricePerHour;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "freelancer")
    private ResumeEntity resume;
}
