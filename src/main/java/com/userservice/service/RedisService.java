package com.userservice.service;

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

    public Object get(String key) {
        try {
            return template.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Exception occured while retriving data from cache : ", e);
            return null;
        }
    }

    public void set(String key, Object obj, Long expiry) {
        try {
            template.opsForValue().set(key, obj, expiry, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception occured while storing data from acache : ", e);
        }

    }

    public void delete(String key){
        this.template.delete(key);
        log.info("User info got removed from cache successfully");
    }

}