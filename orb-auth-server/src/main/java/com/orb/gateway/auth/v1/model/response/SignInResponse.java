package com.orb.gateway.auth.v1.model.response;

import com.orb.gateway.auth.config.security.AuthenticationToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "sign up/in response")
public record SignInResponse(
        String accessToken,
        String refreshToken,
        long refreshTokenIssueVersion,
        SearchMemberResponse member
) {
    public static SignInResponse of(AuthenticationToken token, SearchMemberResponse member) {
        return SignInResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .refreshTokenIssueVersion(token.getRefreshTokenIssueVersion())
                .member(member)
                .build();
    }
}
