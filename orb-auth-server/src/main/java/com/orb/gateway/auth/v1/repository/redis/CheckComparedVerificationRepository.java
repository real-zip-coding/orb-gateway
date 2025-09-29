package com.orb.gateway.auth.v1.repository.redis;

import com.orb.gateway.auth.entity.redis.CheckComparedVerification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheckComparedVerificationRepository extends CrudRepository<CheckComparedVerification, UUID> {
    CheckComparedVerification findByVerificationKey(String verificationKey);
}
