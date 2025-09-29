package com.orb.gateway.auth.v1.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orb.gateway.auth.common.constraint.AccessTokenType;
import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.v1.repository.jpa.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.orb.gateway.auth.common.constraint.RequestHeaderType;
import com.orb.gateway.auth.v1.impl.UserDetailsImpl;
import com.orb.gateway.auth.v1.model.dto.PermissionDTO;
import com.orb.gateway.auth.v1.repository.dsl.AuthProcDSLRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
  // Service
  private final HttpServletRequest servletRequest;

  // Repository
  private final MemberRepository memberRepository;
  private final AuthProcDSLRepository authProcDSLRepository;

  @Override
  public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findMemberEntityByEmailAndMemberStatus(email, Member.MemberStatus.ACTIVE.getCode())
                      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    UserDetailsImpl userDetails = new UserDetailsImpl(member);
    userDetails.setAuthorities(this.getPermission(member.getMemberNo()));
    return userDetails;
  }

  public List<SimpleGrantedAuthority> getPermission(long userId) {
    List<PermissionDTO> permissions = authProcDSLRepository.findRoleByUserId(userId);
    String uuid = servletRequest.getHeader(RequestHeaderType.APP_UID.getHeaderName());    //매번 권한을 체크 할경우

    return permissions.stream()
            .filter(permission -> permission.getPermissionNo() != null)
            .map((map) -> (uuid !=null && uuid.equals(map.getAppUid()))
                    ? "ROLE_" + AccessTokenType.TYPE2
                    : "ROLE_" + AccessTokenType.TYPE1)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }
}