package com.orb.gateway.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class OrbEurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrbEurekaServerApplication.class, args);
    }
}
