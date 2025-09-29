package com.orb.gateway.auth.v1.repository.redis;

import com.orb.gateway.auth.entity.redis.TokenBlackList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenBlackListRepository extends CrudRepository<TokenBlackList, UUID> {
    TokenBlackList findByAccessToken(String accessToken);
}
