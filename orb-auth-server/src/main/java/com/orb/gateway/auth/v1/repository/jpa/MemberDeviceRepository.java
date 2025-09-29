package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.MemberDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDeviceRepository extends JpaRepository<MemberDevice, Long> {
    Optional<MemberDevice> findByMemberNo(Long memberId);
}
