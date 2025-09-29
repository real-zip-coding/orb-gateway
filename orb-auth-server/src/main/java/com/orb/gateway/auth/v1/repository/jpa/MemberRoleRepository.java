package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.entity.mysql.MemberRoleBridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRoleBridge, Long> {
    Optional<MemberRoleBridge> findMemberRoleBridgeByMember(Member member);
}
