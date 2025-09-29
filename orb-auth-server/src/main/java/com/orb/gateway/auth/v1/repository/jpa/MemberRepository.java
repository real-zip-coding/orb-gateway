package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberEntityByEmail(String email);
    Optional<Member> findMemberEntityByEmailAndMemberStatus(String email, int memberStatus);
    List<Member> findMembersByMemberStatusAndSsnNotNull(int memberStatus);
}
