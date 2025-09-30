package com.orb.gateway.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class OrbAuthServerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(OrbAuthServerApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
