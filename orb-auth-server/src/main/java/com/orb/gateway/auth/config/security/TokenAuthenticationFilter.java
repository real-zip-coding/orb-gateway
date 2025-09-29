package com.orb.gateway.auth.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orb.gateway.auth.common.constraint.ErrorType;
import com.orb.gateway.auth.common.exception.Exceptions;
import com.orb.gateway.auth.common.model.CommonResponse;
import com.orb.gateway.auth.entity.redis.TokenBlackList;
import com.orb.gateway.auth.v1.repository.jpa.MemberDeviceRepository;
import com.orb.gateway.auth.v1.repository.redis.TokenBlackListRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationTokenProvider authenticationToken;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        if (this.isPublicURI(requestURI)) {
            filterChain.doFilter(req, res);
            return;
        }

        String token = authenticationToken.parseTokenString(req);
        try {
            if (token != null && authenticationToken.validateToken(token)) {
                TokenBlackList blackList = tokenBlackListRepository.findByAccessToken(token);
                if(!ObjectUtils.isEmpty(blackList))
                    throw new Exceptions.BadCredentialsException("accessToken has expired");

                Claims claims = authenticationToken.getClaim(token);

                Authentication auth = authenticationToken.getAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("Save authentication in SecurityContextHolder. - email: {}", auth.getName());
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.error("Token validation failed. - token: {}, error: {}", token, e.getMessage(), e);
            res.setContentType("application/json");
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);

            ObjectMapper objMapper = new ObjectMapper();
            String errorResponse = objMapper.writeValueAsString(
                    new CommonResponse().resFail(ErrorType.ACCESS_DENIED).getBody()
            );
            res.getWriter().write(errorResponse);
            return;
        }

        filterChain.doFilter(req, res);
    }

    private boolean isPublicURI(String requestURI) {
        return Arrays.stream(SpringSecurityConfig.PUBLIC_URIS)
                .anyMatch(publicURI -> requestURI.matches(publicURI.replace("**", ".*")));
    }
}