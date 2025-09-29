package com.orb.gateway.auth.config.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import com.orb.gateway.auth.v1.model.dto.AuthMemberDeviceInfo;

public interface AuthenticationTokenProvider {

    /***
     * HTTP 요청에서 토큰 취득
     * @param request HTTP 요청
     * @return 토큰
     */
    String parseTokenString(HttpServletRequest request);

    /***
     * 토큰 발급
     * @param email 인증 이메일
     * @return 토큰
     */
    AuthenticationToken createToken(String email, AuthMemberDeviceInfo deviceInfo);


    /***
     * 토큰에서 Claims 취득
     * @param token 토큰
     * @return Claims
     */
    Claims getClaim(String token);

    /***
     * 토큰 유효성 검사
     * @param token 토큰
     * @return 유효여부
     */
    boolean validateToken(String token);

    Authentication getAuthentication(Claims claims);
}