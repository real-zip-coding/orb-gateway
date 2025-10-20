package com.orb.gateway.api.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BlacklistTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<BlacklistTokenGatewayFilterFactory.Config> {
    @Value("${spring.security.oauth2.blacklist-uri}")
    private String blacklistCheckUri;

    private final WebClient.Builder webClientBuilder;

    public BlacklistTokenGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return chain.filter(exchange);
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            return webClientBuilder.build()
                    .get()
                    .uri(blacklistCheckUri)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            return response.setComplete();
                        }
                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {
    }
}
