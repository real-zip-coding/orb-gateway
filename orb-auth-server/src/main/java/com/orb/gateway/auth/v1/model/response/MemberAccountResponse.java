package com.orb.gateway.auth.v1.model.response;

import com.orb.gateway.auth.entity.mysql.MemberAccount;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 연동계좌 정보")
public record MemberAccountResponse(
        @Schema(description = "잔액", example = "100000")
        long balance,
        @Schema(description = "동결 잔액(대기금)", example = "10000")
        Long balanceFrozen,
        @Schema(description = "계좌명", example = "Woori 8912")
        String accountName,
        @Schema(description = "계좌 유형", example = "Savings")
        String accountType
) {
    public static MemberAccountResponse of(MemberAccount memberAccount) {
        return new MemberAccountResponse(
                memberAccount.getMemberAccountBalance(),
                memberAccount.getMemberAccountFrozenBalance(),
                memberAccount.getMemberAccountName(),
                memberAccount.getMemberAccountBankType()
        );
    }
}
