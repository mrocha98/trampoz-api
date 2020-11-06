package br.gov.sp.fatec.trampoz_api.admins;

import br.gov.sp.fatec.trampoz_api.users.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "adm_admin")
@PrimaryKeyJoinColumn(name = "adm_id")
public class AdminEntity extends UserEntity {
}
