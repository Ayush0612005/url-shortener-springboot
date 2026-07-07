package com.ayush.urlshortener.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration CACHE_TTL = Duration.ofHours(24);

    public String getOriginalUrl(String shortCode) {
        return redisTemplate.opsForValue().get(shortCode);
    }

    public void saveOriginalUrl(String shortCode, String originalUrl) {
        redisTemplate.opsForValue()
                .set(shortCode, originalUrl, CACHE_TTL);
    }

    public void delete(String shortCode) {
        redisTemplate.delete(shortCode);
    }
}
