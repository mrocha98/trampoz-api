package br.gov.sp.fatec.trampoz_api.contractors;

import br.gov.sp.fatec.trampoz_api.jobs.JobEntity;
import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "con_contractor")
@PrimaryKeyJoinColumn(name = "con_id")
public class ContractorEntity extends UserEntity {

    @Column(name = "con_cnpj", columnDefinition = "char", length = 14)
    private String cnpj;

    @Column(name = "con_company_name")
    private String companyName;

    @Column(name = "con_company_logo_link", columnDefinition = "text")
    private String companyLogoLink;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contractor")
    private Set<JobEntity> jobs;
}
