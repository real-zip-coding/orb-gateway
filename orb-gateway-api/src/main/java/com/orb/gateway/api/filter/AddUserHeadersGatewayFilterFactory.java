package com.orb.gateway.api.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddUserHeadersGatewayFilterFactory extends AbstractGatewayFilterFactory<AddUserHeadersGatewayFilterFactory.Config> {

    public AddUserHeadersGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(c -> c.getAuthentication())
                .flatMap(authentication -> {
                    if (authentication.getPrincipal() instanceof Jwt) {
                        Jwt jwt = (Jwt) authentication.getPrincipal();
                        return chain.filter(exchange.mutate()
                                .request(builder -> {
                                    builder.header("X-User-ID", jwt.getClaimAsString("memberNo"));
                                    builder.header("X-User-Email", jwt.getClaimAsString("email"));
                                    List<String> roles = jwt.getClaimAsStringList("roleName");
                                    if (roles != null && !roles.isEmpty()) {
                                        builder.header("X-User-Roles", String.join(",", roles));
                                    }
                                })
                                .build());
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange)); // If no authentication, just proceed
    }

    public static class Config {
        // No specific configuration needed for this filter yet
    }
}
