package com.orb.gateway.auth.v1.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.entity.mysql.MemberOauth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Builder
public record SignupRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Member.AuthType authType
){

    public static SignupRequest fromPayload(String email, Member.AuthType authType) {
        return SignupRequest.builder()
                .email(email)
                .authType(authType)
                .build();
    }

    public Member toMemberEntity() {
        return Member.builder()
                        .email(email)
                        .password(
                            Optional.ofNullable(password)
                                .map(new BCryptPasswordEncoder()::encode)   // BCrypt encoding
                                .orElse(null)
                        )
                        .authType(ObjectUtils.isEmpty(authType) ? Member.AuthType.EMAIL : authType)
                        .memberStatus(Member.MemberStatus.ACTIVE.getCode())
                    .build();
    }

    public MemberOauth toMemberOauthEntity(String ci) {
        return MemberOauth.builder()
                .ci(ci)
                .member(toMemberEntity())
                .build();
    }
}
