package com.orb.gateway.auth.entity.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberAuthToken extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_auth_token_no")
    private long authTokenNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_auth_token_type", nullable = false)
    private AuthType authType;

    @ColumnDefault("1")
    @Column(name = "member_auth_token_modify_cnt", nullable = false)
    private int modifyCount;

    @Column(name = "member_auth_token_value", nullable = false, length = 500)
    private String authToken;

    public enum AuthType {
        JWT_REFRESH, JWT_ACCESS, EMAIL_VERIFY, GOOGLE_AUTH, APPLE_AUTH, APP_PUSH
    }

    public void updateToken(String authToken) {
        ++this.modifyCount;
        this.authToken = authToken;
    }
}