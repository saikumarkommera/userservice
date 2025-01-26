package com.userservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate template;

    @Retry(name = "userservice", fallbackMethod = "getFallback")
    @CircuitBreaker(name = "userserivce", fallbackMethod = "getFallback")
    public Object get(String key) {
        return template.opsForValue().get(key);
    }

    @Retry(name = "userservice", fallbackMethod = "setFallback")
    @CircuitBreaker(name = "userserivce", fallbackMethod = "setFallback")
    public void set(String key, Object obj, Long expiry) {
        template.opsForValue().set(key, obj, expiry, TimeUnit.SECONDS);
    }

    @Retry(name = "userservice", fallbackMethod = "deleteFallback")
    @CircuitBreaker(name = "userserivce", fallbackMethod = "deleteFallback")
    public void delete(String key) {
        this.template.delete(key);
        log.info("User info got removed from cache successfully");
    }

    public Object getFallback(String key, Exception ex) {
        log.error("Fallback triggered for get operation (key: {}): {}", key, ex.getMessage());
        return null;
    }

    public void setFallback(String key, Object obj, Long expiry, Exception ex) {
        log.error("Fallback triggered for set operation (key: {}): {}", key, ex.getMessage());
    }

    public void deleteFallback(String key, Exception ex) {
        log.error("Fallback triggered for delete operation (key: {}): {}", key, ex.getMessage());
    }

}