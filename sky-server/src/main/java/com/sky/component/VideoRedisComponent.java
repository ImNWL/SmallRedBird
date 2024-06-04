package com.sky.component;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import com.sky.entity.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VideoRedisComponent {
    @Autowired
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = MessageConstant.KAFKA_TOPIC_VIDEO_SAVE, groupId = "group1")
    public void listernForSave(String str) {
        Video video = JSON.parseObject(str, Video.class);
        log.info("更新至Redis缓存: {}", video);
        redisTemplate.opsForValue().set("video:" + video.getId(), video, 30, TimeUnit.SECONDS);
    }
}
