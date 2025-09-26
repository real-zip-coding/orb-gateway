//package com.orb.gateway.auth.config.db;
//
//import com.querydsl.jpa.JPQLTemplates;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@RequiredArgsConstructor
//@Configuration
//public class QueryDSLConfig {
//    private final EntityManager entityManager;
//
//    @Bean
//    public JPAQueryFactory jpaQueryFactory() {
//        return new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
//    }
//}