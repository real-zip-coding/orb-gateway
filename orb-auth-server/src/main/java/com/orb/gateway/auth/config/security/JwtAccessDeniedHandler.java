package com.orb.gateway.auth.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orb.gateway.auth.common.constraint.ErrorType;
import com.orb.gateway.auth.common.model.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // CommonResponse를 사용하여 에러 응답 작성
        String errorResponse = objectMapper.writeValueAsString(
                new CommonResponse().resFail(ErrorType.ACCESS_DENIED).getBody()
        );

        response.getWriter().write(errorResponse);
    }
}