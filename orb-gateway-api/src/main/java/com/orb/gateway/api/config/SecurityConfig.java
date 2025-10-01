package com.orb.gateway.api.config;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    public static final String[] PUBLIC_URIS = {
            "/auth/**",
            "/actuator/**"
    };

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(PUBLIC_URIS).permitAll() // Permit public URIs
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {
                    try {
                        jwt.jwtDecoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter());
                    } catch (Exception e) {
                        throw new RuntimeException(e);  //TODO Handle exception properly
                    }
                })
            );
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() throws Exception {
        URL jwkSetUrl = new URL(jwkSetUri);

        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwkSetUrl)
                .cache(60000, 10000) // Cache life time of 1 minute, refresh 10 seconds before expiry
                .build();

        return NimbusReactiveJwtDecoder.withJwkSource(
                signedJWT -> {
                    JWSHeader jwsHeader = signedJWT.getHeader();
                    JWKMatcher jwkMatcher = JWKMatcher.forJWSHeader(jwsHeader);
                    JWKSelector jwkSelector = new JWKSelector(jwkMatcher);
                    try {
                        return Flux.fromIterable(jwkSource.get(jwkSelector, null));
                    } catch (KeySourceException e) {
                        throw new RuntimeException(e); //TODO Handle exception properly
                    }
                }
        ).build();
    }

    // Custom converter to map JWT claims to Spring Security authorities
    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roleName"); // Assuming 'roleName' claim holds roles
            if (roles == null) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
