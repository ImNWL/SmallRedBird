package com.sky.component;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserRedisComponent {
    @Autowired
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = MessageConstant.KAFKA_TOPIC_USER_REGISTER, groupId = "group1")
    public void listernForSave(String str) {
        User user = JSON.parseObject(str, User.class);
        log.info("更新至Redis缓存: {}", user);
        redisTemplate.opsForValue().set("user:" + user.getPhone(), user, 30, TimeUnit.SECONDS);
//        redisTemplate.opsForValue().set("user:" + user.getPhone(), user);
    }

    @KafkaListener(topics = MessageConstant.KAFKA_TOPIC_USER_DELETE, groupId = "group1")
    public void listernForDelete(String key) {
        log.info("删除Redis缓存中: {}", key);
        redisTemplate.opsForValue().getAndDelete(key);
    }
}
