package com.orb.gateway.auth.common.model;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ExampleHolder {
    private HttpStatus statusCode;
    private Example holder;
    private int errorCode;
    private String errorMessage;
}