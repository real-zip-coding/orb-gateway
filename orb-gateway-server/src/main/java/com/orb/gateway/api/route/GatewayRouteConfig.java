package com.orb.gateway.api.route;

import com.orb.gateway.api.filter.AddUserHeadersGatewayFilterFactory;
import com.orb.gateway.api.filter.BlacklistTokenGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {
    private final AddUserHeadersGatewayFilterFactory addUserHeadersGatewayFilterFactory;
    private final BlacklistTokenGatewayFilterFactory blacklistTokenGatewayFilterFactory;

    public GatewayRouteConfig(AddUserHeadersGatewayFilterFactory addUserHeadersGatewayFilterFactory, BlacklistTokenGatewayFilterFactory blacklistTokenGatewayFilterFactory) {
        this.addUserHeadersGatewayFilterFactory = addUserHeadersGatewayFilterFactory;
        this.blacklistTokenGatewayFilterFactory = blacklistTokenGatewayFilterFactory;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("orb-auth-server_route", r -> r.path("/auth/**", "/test/**")
                        .filters(f -> f.rewritePath("/auth/(?<segment>.*)", "/v1/auth/${segment}")
                                .rewritePath("/test/(?<segment>.*)", "/test/${segment}")
                                .filter(addUserHeadersGatewayFilterFactory.apply(new AddUserHeadersGatewayFilterFactory.Config()))
                                .filter(blacklistTokenGatewayFilterFactory.apply(new BlacklistTokenGatewayFilterFactory.Config()))
                                .addResponseHeader("X-Gateway", "favy-gateway"))
                        .uri("lb://orb-auth-server"))
                .route("orb-discover-server_route", r -> r.path("/discover/**")
                        .filters(f -> f.rewritePath("/discover/(?<segment>.*)", "/${segment}")
                                .filter(addUserHeadersGatewayFilterFactory.apply(new AddUserHeadersGatewayFilterFactory.Config()))
                                .filter(blacklistTokenGatewayFilterFactory.apply(new BlacklistTokenGatewayFilterFactory.Config()))
                                .addResponseHeader("X-Gateway", "favy-gateway"))
                        .uri("lb://orb-discover-server"))
                .build();
    }
}