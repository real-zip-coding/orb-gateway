package com.orb.gateway.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class OrbGatewayApiApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(OrbGatewayApiApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
