package com.sky.component;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.LikeDTO;
import com.sky.entity.Like;
import com.sky.entity.Video;
import com.sky.exception.BadRegisterException;
import com.sky.mapper.LikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LikeRedisComponent {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LikeMapper likeMapper;

    private ConcurrentHashMap<Long, Long> likedVideos = new ConcurrentHashMap<>();

    // 在使用 @KafkaListener 并设置 concurrency 属性的情况下，每个线程会代表一个独立的消费者实例，但它们都属于同一个消费者组。
    @KafkaListener(topics = MessageConstant.KAFKA_TOPIC_LIKE_SAVE, groupId = "group1", concurrency = "3")
    public void listernForLike(String str) {
        LikeDTO likeDTO = JSON.parseObject(str, LikeDTO.class);
        log.info("异步点赞，收到: {}", likeDTO);

        Long videoId = likeDTO.getVideoId();
        String key = videoId.toString();
        long offset = likeDTO.getUserId() % (MessageConstant.EXPECTED_BITMAP_SIZE * 8);
        if (!redisTemplate.hasKey(key)) {
            // Redis缓存中不存在，从数据库读取
            Like like = likeMapper.select(likeDTO.getVideoId());

            // 同步至Redis缓存
            redisTemplate.execute((RedisCallback<Void>) connection -> {
                connection.set(key.getBytes(StandardCharsets.UTF_8), like.getLikesBitmap());
                return null;
            });
        }

        redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        redisTemplate.opsForValue().setBit(key, offset, true);

        likedVideos.put(videoId, System.currentTimeMillis());
    }

    @Scheduled(cron = "0 * * * * ?")
    public void syncLikesToDatabase() {
        log.info("定时任务");
        likedVideos.forEach((videoId, v) -> {
            log.info("定时任务，同步点赞到数据库: {}", videoId);
            Long updateTime = System.currentTimeMillis();
            synToMySQL(videoId);
            likedVideos.compute(videoId, (k, time) -> time < updateTime ? null : time);
        });
    }

    private void synToMySQL(Long videoId) {
        String key = videoId.toString();
        if (!redisTemplate.hasKey(key)) throw new BadRegisterException(MessageConstant.ERROR);
        byte[] likesBitmap = (byte[]) redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            return connection.get(key.getBytes(StandardCharsets.UTF_8));
        });
        Like like = new Like();
        like.setVideoId(videoId);
        like.setLikesBitmap(likesBitmap);
        log.info("更新点赞: {}", like);
        likeMapper.update(like);
    }


}
