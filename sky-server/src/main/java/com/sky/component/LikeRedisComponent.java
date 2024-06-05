package com.sky.component;

import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.LikeDTO;
import com.sky.entity.Like;
import com.sky.entity.UserLike;
import com.sky.entity.Video;
import com.sky.exception.BadRegisterException;
import com.sky.mapper.LikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
    private ConcurrentHashMap<Long, Long> likedUsers = new ConcurrentHashMap<>();

    // 在使用 @KafkaListener 并设置 concurrency 属性的情况下，每个线程会代表一个独立的消费者实例，但它们都属于同一个消费者组。
    @KafkaListener(topics = MessageConstant.KAFKA_TOPIC_LIKE_SAVE, groupId = "group1", concurrency = "3")
    public void listernForLike(String str) {
        LikeDTO likeDTO = JSON.parseObject(str, LikeDTO.class);
        log.info("异步点赞，收到: {}", likeDTO);

//        Long videoId = likeDTO.getVideoId();
//        String videoKey = videoId.toString();
//        long videoOffset = videoId % (MessageConstant.EXPECTED_BITMAP_SIZE * 8);
//        Long userId = likeDTO.getUserId();
//        String userKey = userId.toString();
//        long userOffset = userId % (MessageConstant.EXPECTED_BITMAP_SIZE * 8);
//
//        if (!redisTemplate.hasKey(videoKey)) {
//            // Redis缓存中不存在，从数据库读取
//            Like like = likeMapper.select(videoId);
//
//            // 同步至Redis缓存
//            redisTemplate.execute((RedisCallback<Void>) connection -> {
//                connection.set(videoKey.getBytes(StandardCharsets.UTF_8), like.getLikesBitmap());
//                return null;
//            });
//        }
//        redisTemplate.opsForValue().setBit(videoKey, userOffset, true);
//        redisTemplate.expire(videoKey, 60, TimeUnit.SECONDS);
//
//
//
//        if (!redisTemplate.hasKey(userKey)) {
//            // Redis缓存中不存在，从数据库读取
//            UserLike userLike = likeMapper.selectUser(userId);
//
//            // 同步至Redis缓存
//            redisTemplate.execute((RedisCallback<Void>) connection -> {
//                connection.set(userKey.getBytes(StandardCharsets.UTF_8), userLike.getLikesBitmap());
//                return null;
//            });
//        }
//        redisTemplate.opsForValue().setBit(userKey, videoOffset, true);
//        redisTemplate.expire(userKey, 60, TimeUnit.SECONDS);
//
//
//        likedVideos.put(videoId, System.currentTimeMillis());
//        likedUsers.put(userId, System.currentTimeMillis());

//        List<Integer> userIds = List.of(1, 5, 9);
//        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
//            String key = "likes:" + videoId;
//            userIds.forEach(userId -> connection.setBit(key.getBytes(), userId, true));
//            return null;
//        });
    }

    @Scheduled(cron = "0 * * * * ?")
    public void syncLikesToDatabase() {
        log.info("定时任务");
        likedVideos.forEach((videoId, v) -> {
            log.info("定时任务，同步点赞到数据库: {}", videoId);
            synLikeVideoToMySQL(videoId);
        });
        likedVideos.clear();

        likedUsers.forEach((userId, v) -> {
            log.info("定时任务，同步点赞到数据库: {}", userId);
            synLikeUserToMySQL(userId);
        });
        likedUsers.clear();
    }

    private void synLikeVideoToMySQL(Long videoId) {
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

    private void synLikeUserToMySQL(Long userId) {
        String key = userId.toString();
        if (!redisTemplate.hasKey(key)) throw new BadRegisterException(MessageConstant.ERROR);
        byte[] likesBitmap = (byte[]) redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            return connection.get(key.getBytes(StandardCharsets.UTF_8));
        });
        UserLike userLike = new UserLike();
        userLike.setUserId(userId);
        userLike.setLikesBitmap(likesBitmap);
        log.info("更新点赞: {}", userLike);
        likeMapper.updateUser(userLike);
    }


}
