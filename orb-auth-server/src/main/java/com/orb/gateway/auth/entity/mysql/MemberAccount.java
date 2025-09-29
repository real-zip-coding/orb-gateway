package com.orb.gateway.auth.entity.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberAccount extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberAccountNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;

    @Column(name = "member_account_bank_name")
    private String memberAccountName;

    @Column(name = "member_account_bank_number")
    private String memberAccountBankNo;

    @Column(name = "member_account_bank_routing_number")
    private String memberAccountBankRoutingNo;

    @Column(name = "member_account_bank_type")
    private String memberAccountBankType;

    private long memberAccountBalance;

    private long memberAccountFrozenBalance;

    private long memberAccountStatus;

    // memberAccountStatus 활성화/비활성화
    public enum MemberAccountStatus {
        ACTIVE(1), INACTIVE(0);
        private final long code;
        MemberAccountStatus(long code) {
            this.code = code;
        }
        public long getCode() {
            return code;
        }
    }

    public void updateBankInfo(String memberAccountName, String memberAccountBankNo, String memberAccountBankRoutingNo, String memberAccountBankType) {
        this.memberAccountName = memberAccountName;
        this.memberAccountBankNo = memberAccountBankNo;
        this.memberAccountBankRoutingNo = memberAccountBankRoutingNo;
        this.memberAccountBankType = memberAccountBankType;
    }

    public void updateStatus(long memberAccountStatus) {
        this.memberAccountStatus = memberAccountStatus;
    }
}