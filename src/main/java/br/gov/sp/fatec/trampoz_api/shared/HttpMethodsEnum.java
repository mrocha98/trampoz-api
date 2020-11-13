package br.gov.sp.fatec.trampoz_api.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HttpMethodsEnum {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    @Getter
    private final String value;
}
