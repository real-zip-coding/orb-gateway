package com.orb.gateway.auth.v1.repository.jpa;

import com.orb.gateway.auth.entity.mysql.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findPermissionByRoleName(Role.MemberType roleName);
}
