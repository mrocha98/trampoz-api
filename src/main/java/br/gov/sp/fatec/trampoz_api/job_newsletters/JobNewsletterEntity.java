package br.gov.sp.fatec.trampoz_api.job_newsletters;

import br.gov.sp.fatec.trampoz_api.jobs.JobEntity;
import br.gov.sp.fatec.trampoz_api.newsletters.NewsletterEntity;
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
@DiscriminatorValue("job")
public class JobNewsletterEntity extends NewsletterEntity {
    public JobNewsletterEntity (String domain) {
        super(domain);
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "jobNewsletter")
    private Set<JobEntity> jobs;
}
