package com.orb.gateway.auth.config;

import com.orb.gateway.auth.config.db.ConfigConst;
import com.orb.gateway.auth.entity.redis.TokenBlackList;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@EnableRedisRepositories(
    basePackages = ConfigConst.EnvPath.REDIS_BASE_PACKAGE
    , enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP
    , keyspaceNotificationsConfigParameter = ""
)
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.password}")
  private String pw;

  private final Environment environment;

  @Bean
  public LettuceConnectionFactory connectionFactory() {
    boolean isDev = ConfigConst.ApplicationConf.DEV_PROFILES
            .stream().anyMatch(environment::acceptsProfiles);
    if(isDev) {
      LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
      lettuceConnectionFactory.setPassword(pw);
      return lettuceConnectionFactory;
    } else {  //prod > --tls --insecure 옵션
      RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
      redisStandaloneConfiguration.setPassword(pw);

      LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
              .useSsl()
              .disablePeerVerification()
              .build();

      return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new StringRedisSerializer());
    return redisTemplate;
  }

  @Component
  public static class SessionExpiredEventListener {
    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<HttpSession> event) {
      if(event.getValue() instanceof TokenBlackList tokenBlackList)
        log.info("TokenBlackList has expired - token:{}", tokenBlackList.getAccessToken());
      else
        log.info("Undefined cached data has expired - uuid:{}", event.getId());
    }
  }
}