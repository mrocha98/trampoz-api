package br.gov.sp.fatec.trampoz_api.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RolesEnum {
    ADMIN("ADMIN"),
    FREELANCER("FREELANCER"),
    CONTRACTOR("CONTRACTOR");

    @Getter
    private final String value;
}
