package com.orb.gateway.api.route;

import com.orb.gateway.api.filter.AddUserHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouteConfig {
    private final AddUserHeadersGatewayFilterFactory addUserHeadersGatewayFilterFactory;

    public GatewayRouteConfig(AddUserHeadersGatewayFilterFactory addUserHeadersGatewayFilterFactory) {
        this.addUserHeadersGatewayFilterFactory = addUserHeadersGatewayFilterFactory;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("orb-auth-server_route", r -> r.path("/auth/**", "/test/**")
                        .filters(f ->
                                f.rewritePath("/auth/(?<segment>.*)", "/v1/auth/${segment}")
                                .rewritePath("/test/(?<segment>.*)", "/test/${segment}")
                                .filter(addUserHeadersGatewayFilterFactory.apply(new AddUserHeadersGatewayFilterFactory.Config()))
                                .addResponseHeader("X-Gateway", "orb-gateway")
                        ).uri("lb://orb-auth-server"))
                .build();
    }
}
