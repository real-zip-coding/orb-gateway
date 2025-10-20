package com.orb.gateway.auth.v1.controller;

import com.orb.gateway.auth.common.annotation.AuthMemberHeaderInfo;
import com.orb.gateway.auth.common.model.CommonResponse;
import com.orb.gateway.auth.config.security.AuthenticationToken;
import com.orb.gateway.auth.v1.service.MemberAccessService;
import com.orb.gateway.auth.v1.model.dto.AuthMemberDeviceInfo;
import com.orb.gateway.auth.v1.model.request.SignInRequest;
import com.orb.gateway.auth.v1.model.response.SignInResponse;
import com.orb.gateway.auth.v1.model.response.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController extends CommonResponse {
    private final MemberAccessService memberAccessService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
            @RequestBody @Valid SignupRequest request
    ) {
        SignInResponse res = memberAccessService.signUp(request);
        return this.resSuccess(res);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(
            @RequestBody @Valid SignInRequest signInRequest,
            @AuthMemberHeaderInfo AuthMemberDeviceInfo deviceInfo
    ) {
        SignInResponse res = memberAccessService.signIn(signInRequest.getEmail(), signInRequest.getPassword(), deviceInfo);
        return this.resSuccess(res);
    }

    @PutMapping("/reissue")
    public ResponseEntity<?> reissue(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("RefreshToken") String refreshToken,
            @AuthMemberHeaderInfo AuthMemberDeviceInfo deviceInfo
    ) {
        AuthenticationToken reissuedToken = memberAccessService.reissue(accessToken, refreshToken, deviceInfo);
        return this.resSuccess(reissuedToken);
    }

    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> signOut(
            @RequestHeader("Authorization") String accessToken
    ) {
        memberAccessService.signOut(accessToken);
        return this.resSuccessNoContents();
    }
}