package com.orb.gateway.auth.entity.mysql;

import jakarta.persistence.*;
import lombok.*;
import com.orb.gateway.auth.common.constraint.OsType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberLoginHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private long memberLoginHistoryNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Column(name = "member_login_type")
    private String loginType;

    @Column(name = "member_device_app_uuid_value", nullable = false)
    private String appUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_device_os_type")
    private OsType osType;

    @Column(name = "member_device_os_version")
    private String osVersion;

//    private boolean isDelete;

    @Column(name = "member_device_build_version")
    private String buildVersion;
}