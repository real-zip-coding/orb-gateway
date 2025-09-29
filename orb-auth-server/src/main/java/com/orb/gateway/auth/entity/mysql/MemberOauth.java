package com.orb.gateway.auth.entity.mysql;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberOauth extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberOauthNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false, unique = true)
    private Member member;

    @Column(name = "member_oauth_ci_value", nullable = false)
    private String ci;
}