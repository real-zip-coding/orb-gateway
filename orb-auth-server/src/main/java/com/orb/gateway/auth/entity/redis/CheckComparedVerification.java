package com.orb.gateway.auth.entity.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@Builder
@RedisHash(value = "check-compared-verification", timeToLive = 300)
public class CheckComparedVerification {
    @Id
    @Indexed
    private String verificationKey;
    private LocalDateTime comparedAt;
}
