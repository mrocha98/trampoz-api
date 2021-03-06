package br.gov.sp.fatec.trampoz_api.tags;

import br.gov.sp.fatec.trampoz_api.jobs.JobEntity;
import br.gov.sp.fatec.trampoz_api.shared.AutoGeneratedId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "tag_tag")
@Entity
@AttributeOverride(name = "id", column = @Column(name = "tag_id"))
public class TagEntity extends AutoGeneratedId {
    public TagEntity(String title, String type) {
        this.setTitle(title);
        this.setType(type);
    }

    @Column(name = "tag_title")
    private String title;

    @Column(name = "tag_type")
    private String type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tpj_tag_per_job",
            joinColumns = {@JoinColumn(name = "tag_tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "job_job_id")}
    )
    private Set<JobEntity> jobs;
}
