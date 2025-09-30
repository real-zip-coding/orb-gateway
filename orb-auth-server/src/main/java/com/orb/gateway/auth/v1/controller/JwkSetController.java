package com.orb.gateway.auth.v1.controller;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class JwkSetController {
    private final KeyPair keyPair;

    @GetMapping("/jwks")
    public Map<String, Object> jwks() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        JWK jwk = new RSAKey.Builder(publicKey)
                .keyID(UUID.randomUUID().toString()) // Unique ID for the key
                .build();
        return new JWKSet(jwk).toJSONObject();
    }
}