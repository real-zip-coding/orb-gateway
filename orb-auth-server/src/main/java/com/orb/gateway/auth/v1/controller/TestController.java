package com.orb.gateway.auth.v1.controller;

import com.orb.gateway.auth.common.model.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/test")
@RestController
@RequiredArgsConstructor
public class TestController extends CommonResponse {
    @GetMapping
    public ResponseEntity<?> test() {
        return this.resSuccessNoContents();
    }
}