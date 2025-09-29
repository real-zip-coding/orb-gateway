package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.MemberPasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPasswordHistoryRepository extends JpaRepository<MemberPasswordHistory, Long> {}
