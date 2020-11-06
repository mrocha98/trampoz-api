package br.gov.sp.fatec.trampoz_api.marketing_newsletters;

import br.gov.sp.fatec.trampoz_api.newsletters.NewsletterEntity;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("marketing")
public class MarketingNewsletterEntity extends NewsletterEntity {
    public MarketingNewsletterEntity (String domain) {
        super(domain);
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "marketingNewsletter")
    private Set<UserEntity> users;
}
