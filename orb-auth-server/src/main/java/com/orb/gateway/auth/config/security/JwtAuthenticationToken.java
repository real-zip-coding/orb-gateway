package com.orb.gateway.auth.config.security;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtAuthenticationToken implements AuthenticationToken {
    private String accessToken;
    private String refreshToken;
    private long refreshTokenIssueVersion;
}