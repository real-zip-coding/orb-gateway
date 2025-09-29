package com.orb.gateway.auth.v1.controller;

import com.orb.gateway.auth.common.annotation.AuthMemberHeaderInfo;
import com.orb.gateway.auth.common.model.CommonResponse;
import com.orb.gateway.auth.config.security.MemberAccessService;
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

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        return this.resSuccessNoContents();
    }
}