package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.entity.mysql.MemberOauth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOauthRepository extends JpaRepository<MemberOauth, Long> {
    MemberOauth findByMember_EmailAndMember_AuthTypeAndMember_MemberStatus(String email, Member.AuthType authType, int memberStatus);
    MemberOauth findByMember_Email(String email);
}
