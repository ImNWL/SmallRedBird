package com.sky.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisLockComponent {
    @Autowired
    private RedisTemplate redisTemplate;

    // 尝试获取锁
    public boolean tryLock(String key, String value, long timeout, TimeUnit unit) {
        Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        return isLocked != null && isLocked;
    }

    // 释放锁
    public void releaseLock(String key, String value) {
        String currentValue = (String) redisTemplate.opsForValue().get(key);
        if (currentValue != null && currentValue.equals(value)) {
            redisTemplate.delete(key);
        }
    }
}
