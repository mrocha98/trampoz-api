package br.gov.sp.fatec.trampoz_api.jobs;

import br.gov.sp.fatec.trampoz_api.contractors.ContractorEntity;
import br.gov.sp.fatec.trampoz_api.job_newsletters.JobNewsletterEntity;
import br.gov.sp.fatec.trampoz_api.shared.AutoGeneratedId;
import br.gov.sp.fatec.trampoz_api.tags.TagEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "job_job")
@Entity
@AttributeOverride(name = "id", column = @Column(name = "job_id"))
public class JobEntity extends AutoGeneratedId {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "con_id")
    private ContractorEntity contractor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jnew_id")
    private JobNewsletterEntity jobNewsletter;

    @Column(name = "job_publishing_date")
    private Date publishingDate;

    @Column(name = "job_description", columnDefinition = "text")
    private String description;

    @Column(name = "job_state", length = 2)
    private String state;

    @Column(name = "job_city", columnDefinition = "text")
    private String city;

    @Column(name = "job_is_remote")
    private Boolean isRemote;

    @Column(name = "job_is_open")
    private Boolean isOpen;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tpj_tag_per_job",
            joinColumns = {@JoinColumn(name = "job_job_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_tag_id")}
    )
    private Set<TagEntity> tags;
}
