package com.orb.gateway.auth.entity.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Builder
@RedisHash(value="token-blacklist", timeToLive = 180)   //TODO timeToLive > 추후 accessToken expired 참조
public class TokenBlackList {
    @Id
    private UUID id;
    @Indexed
    private String accessToken;

    public static TokenBlackList to(String requestAccessToken) {
        return TokenBlackList.builder()
                .accessToken(requestAccessToken)
                .build();
    }
}