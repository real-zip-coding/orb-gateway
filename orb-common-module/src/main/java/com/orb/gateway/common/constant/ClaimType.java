package com.orb.gateway.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimType {
    EMAIL("email"),
    MEMBER_ID("memberId"),
    ROLE_NAME("roleName"),
    APP_UID("appUid"),
    DEVICE_AUTH("deviceAuth"),
    TOKEN_TYPE("tokenType");
    private final String messageKey;
}
