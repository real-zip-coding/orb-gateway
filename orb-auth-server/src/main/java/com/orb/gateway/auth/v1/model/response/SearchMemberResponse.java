package com.orb.gateway.auth.v1.model.response;

import com.orb.gateway.auth.entity.mysql.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 검색 응답")
public class SearchMemberResponse {
    private Long memberNo;
    private String name;
    private String email;
    private String phoneNumber;
    private Member.AuthType authType;
    private boolean signed;
    private Member.MemberStatus status;
    private MemberAccountResponse bankAccount;

    public static SearchMemberResponse of(Member member) {
        return SearchMemberResponse.builder()
                .memberNo(member.getMemberNo())
                .name(member.getName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .authType(member.getAuthType())
                .signed(true)
                .build();
    }
}
