package com.orb.gateway.common.model;

import io.jsonwebtoken.Claims;
import lombok.*;
import com.orb.gateway.common.entity.mysql.Role;
import com.orb.gateway.common.constant.ClaimType;

@Data
@Builder
@NoArgsConstructor
public class AuthInfoDTO {
    private Long memberNo;
    private String email;
    private Role.MemberType roleName;
    private String appUid;
    private int modifyCount;

    public AuthInfoDTO(Long memberNo, String email, Role.MemberType roleName, String appUid, int modifyCount) {
        this.memberNo = memberNo;
        this.email = email;
        this.roleName = roleName;
        this.appUid = appUid;
        this.modifyCount = modifyCount;
    }

    private AuthInfoDTO(Claims claims) {
        this.memberNo = claims.get(ClaimType.MEMBER_ID.getMessageKey(), Long.class);
        this.email = claims.get(ClaimType.EMAIL.getMessageKey(), String.class);
        this.roleName = Role.MemberType.valueOf(claims.get(ClaimType.ROLE_NAME.getMessageKey(), String.class));
        this.appUid = claims.get(ClaimType.APP_UID.getMessageKey(), String.class);
    }

    public static AuthInfoDTO fromClaims(Claims claims) {
        return new AuthInfoDTO(claims);
    }
}