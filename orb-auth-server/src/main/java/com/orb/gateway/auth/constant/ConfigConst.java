package com.orb.gateway.auth.constant;

public class ConfigConst {
    public static final String MASTER_DATASOURCE = "mucaDataSource";

    public static class JPA {
        public static final String ENTITY_MANAGER_FACTORY_REF = "mucaJpaEntityManagerFactory";
        public static final String TRANSACTION_MANAGER_REF = " mucaTransactionManager";
    }

    public static class EnvPath {
        //JPA
        public static final String _commonPackage = "gateway.common";
        public static final String _basePackage = "com.orb.gateway.auth";
        public static final String _basePackage_V1 = _basePackage + ".v1";
        public static final String REPOSITORY_PACKAGE = _commonPackage + ".repository.jpa";
        public static final String ENTITY_PACKAGE = _commonPackage + ".entity.mysql";

        //Redis
        public static final String REDIS_BASE_PACKAGE = _commonPackage + ".repository.redis";
    }

    public static class ApplicationConf {
        public static final String MASTER_DATASOURCE_PATH = "spring.datasource";
    }
}