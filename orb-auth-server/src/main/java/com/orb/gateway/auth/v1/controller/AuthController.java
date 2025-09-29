package com.orb.gateway.auth.v1.controller;

import com.orb.gateway.auth.config.security.AuthenticationToken;
import com.orb.gateway.auth.config.security.AuthenticationTokenProvider;
import com.orb.gateway.auth.v1.model.dto.LoginRequestDTO;
import com.orb.gateway.auth.v1.model.dto.AuthMemberDeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationTokenProvider authenticationTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationToken> login(
            @RequestBody LoginRequestDTO loginRequestDTO,
            @RequestHeader(value = "OsType", required = false) String osType,
            @RequestHeader(value = "BuildVersion", required = false) String buildVersion,
            @RequestHeader(value = "AppUid", required = false) String appUid,
            @RequestHeader(value = "OsVersion", required = false) String osVersion,
            @RequestHeader(value = "DeviceModel", required = false) String deviceModel
    ) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        // 1. AuthenticationManager를 통해 인증 시도
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. 인증 정보를 기반으로 JWT 토큰 생성
        AuthMemberDeviceInfo deviceInfo = AuthMemberDeviceInfo.of(osType, buildVersion, appUid, osVersion, deviceModel);
        AuthenticationToken jwt = authenticationTokenProvider.createToken(loginRequestDTO.getEmail(), deviceInfo);

        return ResponseEntity.ok(jwt);
    }
}