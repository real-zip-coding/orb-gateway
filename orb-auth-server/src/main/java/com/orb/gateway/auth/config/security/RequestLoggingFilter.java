package com.orb.gateway.auth.config.security;

import com.orb.gateway.auth.config.db.ConfigConst;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final ThreadLocal<UUID> requestIdHolder = new ThreadLocal<>();
    private final Environment environment;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청을 ContentCachingRequestWrapper로 래핑하여 본문을 캐시
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        requestIdHolder.set(UUID.randomUUID());

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            requestIdHolder.remove();
//            this.logRequest(requestWrapper, response);    //현재 필요 없음, 추후 필요시 사용
        }
    }

    /**
     * 현재 요청의 UUID를 반환합니다.
     *
     * @return 현재 요청의 UUID
     */
    public static UUID getCurrentRequestId() {
        return requestIdHolder.get();
    }

    /**
     * 요청과 응답을 기반으로 로그 메시지를 생성하고 로그에 기록합니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    private void logRequest(ContentCachingRequestWrapper request, HttpServletResponse response) {
        boolean isDev = this.isDevelopmentEnvironment();
        String logMessage = this.buildLogMessage(request, response, isDev);

        if (HttpStatus.valueOf(response.getStatus()).isError())
            log.error(logMessage);
        else
            log.info(logMessage);
    }

    /**
     * 요청과 응답을 기반으로 로그 메시지를 생성합니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param isDev    개발 환경 여부
     * @return 로그 메시지
     */
    private String buildLogMessage(ContentCachingRequestWrapper request, HttpServletResponse response, boolean isDev) {
        String requestUri = this.buildRequestUri(request);
        String headers = this.getRequestHeaders(request);
        String requestBody = new String(request.getContentAsByteArray());

        if (isDev)
            return String.format(
                    "\n%s\n[%d]\t%s %s\n%s\n%s",
                    getCurrentRequestId(),
                    response.getStatus(),
                    request.getMethod(),
                    requestUri,
                    headers,
                    requestBody
            );
        else
            return String.format(
                    "[%s] [%d] %s %s %s %s",
                    getCurrentRequestId(),
                    response.getStatus(),
                    request.getMethod(),
                    requestUri,
                    headers.replace("\n", " ").trim(),
                    requestBody.trim()
            );

    }

    /**
     * 요청의 전체 URI를 생성합니다. 쿼리 파라미터를 포함합니다.
     *
     * @param request HTTP 요청
     * @return 전체 요청 URI
     */
    private String buildRequestUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        return StringUtils.isNotBlank(queryString) ? requestUri + "?" + queryString : requestUri;
    }

    /**
     * 요청 헤더를 문자열 형식으로 생성합니다.
     *
     * @param request HTTP 요청
     * @return 요청 헤더 문자열
     */
    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                headers.append(headerName)
                        .append(": ")
                        .append(request.getHeader(headerName))
                        .append("\n")
        );
        return headers.toString();
    }

    /**
     * 현재 환경이 개발 환경 확인
     *
     * @return 개발 환경 true / false
     */
    private boolean isDevelopmentEnvironment() {
        return ConfigConst.ApplicationConf.DEV_PROFILES
                .stream().anyMatch(environment::acceptsProfiles);
    }
}
