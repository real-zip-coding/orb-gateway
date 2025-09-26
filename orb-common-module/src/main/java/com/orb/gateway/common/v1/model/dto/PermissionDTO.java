package com.orb.gateway.common.v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.orb.gateway.common.entity.mysql.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private Long permissionNo;
    private Role.MemberType permissionName;
    private String appUid;
}