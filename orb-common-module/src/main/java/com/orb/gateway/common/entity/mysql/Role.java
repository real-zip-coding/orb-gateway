package com.orb.gateway.common.entity.mysql;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Role extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_no")
    private long roleNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role.MemberType roleName;

    public enum MemberType {
        UNVERIFIED, VERIFIED
    }
}