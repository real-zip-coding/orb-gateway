package com.orb.gateway.auth.v1.model.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}