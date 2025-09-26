//package com.orb.gateway.auth.config.db;
//
//import com.orb.gateway.auth.constant.ConfigConst;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//
//@EnableJpaRepositories(
//        basePackages = ConfigConst.EnvPath.REPOSITORY_PACKAGE,
//        entityManagerFactoryRef = ConfigConst.JPA.ENTITY_MANAGER_FACTORY_REF,
//        transactionManagerRef = ConfigConst.JPA.TRANSACTION_MANAGER_REF
//)
//@EnableTransactionManagement
//@Configuration
//public class DBConfig {
//    @Primary
//    @Bean(name = ConfigConst.MASTER_DATASOURCE)
//    @ConfigurationProperties(prefix = ConfigConst.ApplicationConf.MASTER_DATASOURCE_PATH)
//    public DataSource masterDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Primary
//    @Bean(name = ConfigConst.JPA.ENTITY_MANAGER_FACTORY_REF)
//    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory(
//            EntityManagerFactoryBuilder builder,
//            @Qualifier(ConfigConst.MASTER_DATASOURCE) DataSource dataSource
//    ) {
//        return builder.dataSource(dataSource)
//                .packages(ConfigConst.EnvPath.ENTITY_PACKAGE)
//                .build();
//    }
//
//    @Primary
//    @Bean(name = ConfigConst.JPA.TRANSACTION_MANAGER_REF)
//    public PlatformTransactionManager transactionManager(
//            @Qualifier(ConfigConst.JPA.ENTITY_MANAGER_FACTORY_REF) LocalContainerEntityManagerFactoryBean mfBean
//    ) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(mfBean.getObject());
//        return transactionManager;
//    }
//}