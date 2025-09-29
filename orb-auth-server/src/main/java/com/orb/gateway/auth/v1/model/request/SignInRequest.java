package com.orb.gateway.auth.v1.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignInRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}