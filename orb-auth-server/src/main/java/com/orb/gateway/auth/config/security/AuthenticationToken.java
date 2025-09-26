package com.orb.gateway.auth.config.security;

public interface AuthenticationToken {
    String getAccessToken();
    String getRefreshToken();
    long getRefreshTokenIssueVersion();

}