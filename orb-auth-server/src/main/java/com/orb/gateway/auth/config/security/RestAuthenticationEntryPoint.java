package com.orb.gateway.auth.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orb.gateway.auth.common.constraint.ErrorType;
import com.orb.gateway.auth.common.model.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증오류 handler
 */
@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // CommonResponse를 사용하여 에러 응답 작성
        String errorResponse = objectMapper.writeValueAsString(
                new CommonResponse().resFail(ErrorType.ACCESS_DENIED).getBody()
        );

        response.getWriter().write(errorResponse);
    }
}