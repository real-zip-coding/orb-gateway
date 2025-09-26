package com.orb.gateway.common.entity.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;


@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicInsert
@DynamicUpdate
@Entity
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberNo;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_phone")
    private String phoneNumber;

    @Column(name = "member_ssn")
    private String ssn;

    @ColumnDefault("'EMAIL'")
    @Enumerated(EnumType.STRING)
    @Column(name = "member_login_type", nullable = false)
    private AuthType authType;

    @Column(name = "member_img_uri", nullable = false)
    private String imgUri;

    @Column(name = "member_nickname", nullable = false)
    private String nickname;

    @Builder.Default
    @Column(name="member_status", nullable = false)
    private int memberStatus = MemberStatus.ACTIVE.getCode();

    @Column(name = "member_notification_check_date")
    private LocalDateTime notificationCheck; // 마지막 알림 확인 일시. 하드런칭 이후 제거

    public enum AuthType {
        EMAIL, GOOGLE, APPLE;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void updateSSN(String ssn) {
        this.ssn = ssn;
    }

    public void updateProfile(String nickname, String imgUri) {
        this.nickname = nickname;
        this.imgUri = imgUri;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNotificationCheck() {
        this.notificationCheck = LocalDateTime.now();
    }

    public void updateMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus.getCode();
    }

    public void updateMemberDataNull() {
        this.name = StringUtils.EMPTY;
        this.email = StringUtils.EMPTY;
        this.password = StringUtils.EMPTY;
        this.phoneNumber = StringUtils.EMPTY;
        this.ssn = StringUtils.EMPTY;
        this.imgUri = StringUtils.EMPTY;
        this.nickname = StringUtils.EMPTY;
        this.notificationCheck = null;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MemberStatus {
        ACTIVE(1),
        INACTIVE(2),
        DELETED(0);
        ;
        private final int code;

        public static MemberStatus of(int code) {
            for (MemberStatus status : values()) {
                if (status.getCode() == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid MemberStatus code: " + code);
        }
    }
}