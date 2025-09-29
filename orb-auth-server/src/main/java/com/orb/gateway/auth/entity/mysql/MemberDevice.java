package com.orb.gateway.auth.entity.mysql;

import jakarta.persistence.*;
import lombok.*;
import com.orb.gateway.auth.common.constraint.OsType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberDevice extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberDeviceNo;

    private Long memberNo;

    @Column(name="member_device_app_uuid_value", nullable = false)
    private String appUid;

    @Enumerated(EnumType.STRING)
    @Column(name="member_device_os_type", nullable = false)
    private OsType osType;

    @Column(name="member_device_os_version", nullable = false)
    private String osVersion;

    @Column(name="member_device_build_version", nullable = false)
    private String buildVersion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberNo", nullable = false, updatable = false, insertable = false)
    private Member member;

    public static MemberDevice create(MemberLoginHistory memberLoginHistory) {
        return MemberDevice.builder()
                .memberNo(memberLoginHistory.getMember().getMemberNo())
                .appUid(memberLoginHistory.getAppUid())
                .osType(memberLoginHistory.getOsType())
                .osVersion(memberLoginHistory.getOsVersion())
                .buildVersion(memberLoginHistory.getBuildVersion())
                .build();
    }

    public void update(MemberLoginHistory memberLoginHistory) {
        this.memberNo = memberLoginHistory.getMember().getMemberNo();
        this.appUid = memberLoginHistory.getAppUid();
        this.osType = memberLoginHistory.getOsType();
        this.osVersion = memberLoginHistory.getOsVersion();
        this.buildVersion = memberLoginHistory.getBuildVersion();
    }

}