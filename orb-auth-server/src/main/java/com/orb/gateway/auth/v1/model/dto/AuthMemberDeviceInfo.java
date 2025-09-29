package com.orb.gateway.auth.v1.model.dto;

import lombok.Builder;
import com.orb.gateway.auth.common.constraint.OsType;
import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.entity.mysql.MemberLoginHistory;

@Builder
public record AuthMemberDeviceInfo(
        OsType osType,
        String buildVersion,
        String appUid,
        String osVersion,
        String deviceModel
) {
    public static AuthMemberDeviceInfo of(
            String os,
            String buildVersion,
            String appUid,
            String osVersion,
            String deviceModel
    ) {
        return AuthMemberDeviceInfo.builder()
                .osType(OsType.getOsType(os))
                .buildVersion(buildVersion)
                .appUid(appUid)
                .osVersion(osVersion)
                .deviceModel(deviceModel)
                .build();
    }

    public MemberLoginHistory toMemberLoginHistoryEntity(Member member) {
        return MemberLoginHistory.builder()
                .member(member)
                .loginType(String.valueOf(member.getAuthType()))
                .appUid(appUid)
                .osType(osType)
                .osVersion(osVersion)
                .build();
    }

}