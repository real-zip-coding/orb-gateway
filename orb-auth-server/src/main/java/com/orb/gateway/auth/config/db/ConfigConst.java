package com.orb.gateway.auth.config.db;

import java.util.Set;

public class ConfigConst {
    public static final String MASTER_DATASOURCE = "orbDataSource";

    public static class JPA {
        public static final String ENTITY_MANAGER_FACTORY_REF = "orbJpaEntityManagerFactory";
        public static final String TRANSACTION_MANAGER_REF = " orbTransactionManager";
    }

    public static class EnvPath {
        //JPA
        public static final String _basePackage = "com.orb.gateway.auth";
        public static final String _basePackage_V1 = _basePackage + ".v1";
        public static final String REPOSITORY_PACKAGE = _basePackage_V1 + ".repository.jpa";
        public static final String ENTITY_PACKAGE = _basePackage + ".entity.mysql";

        //Redis
        public static final String REDIS_BASE_PACKAGE = _basePackage + ".repository.redis";
    }

    public static class ApplicationConf {
        public static final String MASTER_DATASOURCE_PATH = "spring.datasource";
        public static final Set<String> DEV_PROFILES = Set.of("dev", "local", "qa");
        public static final Set<String> LOCAL_PROFILES = Set.of("local", "localdev");
    }
}