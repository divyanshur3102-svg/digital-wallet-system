package com.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final Duration EXPIRY = Duration.ofHours(24);
    
    public boolean isProcessed(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }
    
    public void markAsProcessed(String key, Object response) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        redisTemplate.opsForValue().set(redisKey, response, EXPIRY);
    }
    
    public Object getResponse(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        return redisTemplate.opsForValue().get(redisKey);
    }
}