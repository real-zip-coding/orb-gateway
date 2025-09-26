package com.orb.gateway.auth.config.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.orb.gateway.common.entity.mysql.Member;
import com.orb.gateway.common.entity.mysql.Role;
import com.orb.gateway.common.exception.Exceptions;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.orb.gateway.common.model.AuthMemberDeviceInfo;
import com.orb.gateway.common.constant.ClaimType;
import com.orb.gateway.common.constraint.AccessTokenType;
import com.orb.gateway.auth.v1.impl.UserDetailsImpl;
import com.orb.gateway.common.model.AuthInfoDTO;
import com.orb.gateway.auth.v1.repository.dsl.AuthProcDSLRepository;
import com.orb.gateway.auth.v1.service.CustomUserDetailService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider implements AuthenticationTokenProvider {     // JWT 토큰을 생성 및 검증 모듈
    @Value("${jwt.secret}") private String SECRET_KEY;
    @Value("${jwt.expiredTime}") private long TOKEN_EXPIRED_TIME;
    @Value("${jwt.refreshExpiredTime}") private long REFRESH_TOKEN_EXPIRED_TIME;

    private final AuthProcDSLRepository authProcDSLRepository;
    private final CustomUserDetailService customUserDetailService;

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 문자열. "Bearer " 접두사가 포함된 경우에는 이를 제거한 후 반환합니다.
     */
    @Override
    public String parseTokenString(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7);
        return null;
    }

    /**
     * 주어진 이메일로 JWT 토큰을 생성합니다.
     * @param email 토큰을 발급할 이메일
     * @return 생성된 AuthenticationToken 객체, 액세스 토큰과 리프레시 토큰이 포함됩니다.
     */
    @Override
    public AuthenticationToken createToken(String email, AuthMemberDeviceInfo deviceInfo) {
        AuthInfoDTO authInfo = authProcDSLRepository.findMemberByEmailForAuthProc(email)
                .orElseThrow(() -> new UsernameNotFoundException("not found User Info with email: " + email));
        long unixTime = System.currentTimeMillis() / 1000L;
        return JwtAuthenticationToken
                .builder()
                .accessToken(this.buildAccessToken(authInfo, deviceInfo))
                .refreshToken(this.buildRefreshToken())
                .refreshTokenIssueVersion(unixTime)
                .build();
    }

    /**
     * JWT 토큰에서 사용자 정보를 추출합니다.
     * @param token JWT 토큰 문자열
     * @return 추출된 클레임
     */
    @Override
    public Claims getClaim(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();   //토큰이 만료되었더라도 사용자정보 추출
        } catch (Exception e) {
            throw new Exceptions.NotFoundAuth();
        }
    }

    /**
     * JWT 토큰의 유효성을 검사합니다.
     * @param token JWT 토큰 문자열
     * @return 토큰 유효성 여부
     */
    @Override
    public boolean validateToken(String token) {
        if (ObjectUtils.isNotEmpty(token)) {
            try {
                Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
                return true;
            } catch (SignatureException e) {
                log.error("Invalid JWT signature", e.getMessage(), e);
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT token", e.getMessage(), e);
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token", e.getMessage(), e);
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT token", e.getMessage(), e);
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty.", e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * 클레임 정보를 기반으로 Authentication 객체를 생성합니다.
     * @param claims JWT 클레임
     * @return 생성된 Authentication 객체
     */
    public Authentication getAuthentication(Claims claims) {
        AuthInfoDTO authInfoDTO = AuthInfoDTO.fromClaims(claims);
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(
                Member.builder()
                        .memberNo(authInfoDTO.getMemberNo())
                        .email(authInfoDTO.getEmail())
                        .build()
        );

        if(!ObjectUtils.isEmpty(authInfoDTO.getRoleName())) {
            List<SimpleGrantedAuthority> authorities =  customUserDetailService.getPermission(authInfoDTO.getMemberNo());
            userDetailsImpl.setAuthorities(authorities);
        }

        return new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities());
    }

    /**
     * 액세스 토큰을 생성합니다.
     * @param authInfoDTO 인증 정보 DTO
     * @return 생성된 액세스 토큰 문자열
     */
    private String buildAccessToken(AuthInfoDTO authInfoDTO, AuthMemberDeviceInfo deviceInfo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plus(TOKEN_EXPIRED_TIME, ChronoUnit.MILLIS);

        log.info("buildAccessToken authInfoDTO: {}", authInfoDTO);

        // 토큰 발급 타입 확인
        boolean isDeviceAuth = !ObjectUtils.isEmpty(authInfoDTO.getAppUid()) && authInfoDTO.getAppUid().equals(deviceInfo.appUid());
        boolean isVerified = Role.MemberType.VERIFIED.equals(authInfoDTO.getRoleName());
        AccessTokenType accessTokenType =  (isDeviceAuth && isVerified) ? AccessTokenType.TYPE2 : AccessTokenType.TYPE1;

        JwtBuilder jwtBuilder = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setSubject("atk")
                .claim(ClaimType.MEMBER_ID.getMessageKey(), authInfoDTO.getMemberNo())
                .claim(ClaimType.EMAIL.getMessageKey(), authInfoDTO.getEmail())
                .claim(ClaimType.ROLE_NAME.getMessageKey(), authInfoDTO.getRoleName())
                .claim(ClaimType.DEVICE_AUTH.getMessageKey(), isDeviceAuth)
                .claim(ClaimType.TOKEN_TYPE.getMessageKey(), accessTokenType)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY);

        if (isDeviceAuth)
            jwtBuilder.claim(ClaimType.APP_UID.getMessageKey(), authInfoDTO.getAppUid());

        return jwtBuilder.compact();
    }

    /**
     * 리프레시 토큰을 생성합니다.
     * @return 생성된 리프레시 토큰 문자열
     */
    private String buildRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plus(REFRESH_TOKEN_EXPIRED_TIME, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setSubject("rtk")
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

}