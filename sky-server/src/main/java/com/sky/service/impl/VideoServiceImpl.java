package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.component.RedisLockComponent;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.Like;
import com.sky.entity.User;
import com.sky.entity.Video;
import com.sky.exception.BadRegisterException;
import com.sky.mapper.LikeMapper;
import com.sky.mapper.UserMapper;
import com.sky.mapper.VideoMapper;
import com.sky.service.UserService;
import com.sky.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;


@Service
@Slf4j
public class VideoServiceImpl implements VideoService {
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    @Qualifier("kafkaTemplateWithLatency")
    private KafkaTemplate kafkaTemplateWithLatency;

    @Override
    @Transactional
    public void upload(VideoUploadDTO videoUploadDTO) {
        List<MultipartFile> videos = videoUploadDTO.getVideos();
        String title = videoUploadDTO.getTitle();
        List<String> videoUrls = new ArrayList<>();
        if (videos != null && !videos.isEmpty()) {
            List<Future<String>> futures = new ArrayList<>();

            for (MultipartFile video : videos) {
                // 提交上传任务到线程池
                Future<String> future = executorService.submit(() -> {
                    String videoUrl = uploadVideo(video);
                    return videoUrl;
                });
                futures.add(future);
            }

            // 等待所有上传任务完成
            for (Future<String> future : futures) {
                try {
                    String videoUrl = future.get();
                    if (videoUrl == null) throw new BadRegisterException(MessageConstant.UPLOAD_FAILED);
                    // 这会阻塞直到任务完成
                    videoUrls.add(future.get());
                } catch (Exception e) {
                    throw new BadRegisterException(MessageConstant.UPLOAD_FAILED);
                }
            }
        } else {
            throw new BadRegisterException(MessageConstant.UPLOAD_FAILED);
        }

        for (String videoUrl : videoUrls) {
            Video video = new Video();
            video.setAuthorId(BaseContext.getCurrentId());
            video.setTitle(title);
            video.setVideoUrl(videoUrl);
            // 执行数据库操作
            log.info("存储视频至MySQL：{}", video);
            videoMapper.insert(video);

            Like like = new Like();
            like.setVideoId(video.getId());
            like.setLikesBitmap(new byte[MessageConstant.EXPECTED_BITMAP_SIZE]);
            // 执行数据库操作
            log.info("存储点赞至MySQL：{}", like);
            likeMapper.insert(like);

            // Todo 异步同步至Redis
        }

    }

    private String uploadVideo(MultipartFile video) {
        String originalFileName = video.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String videoFileName = UUID.randomUUID().toString() + extension;

        log.info("视频改名：{} -> {}", originalFileName, videoFileName);

        // Todo 上传文件到云端，并获得url
        // String filePath = aliOssUtil.upload(video.getBytes(), videoFileName);
        return videoFileName;
    }

//    private void saveVideoToRedis(Video video) {
//        // 消息队列，同步更改至Redis
//        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_VIDEO_SAVE, video.getId().toString(), JSON.toJSONString(video)).addCallback(new ListenableFutureCallback() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_VIDEO_SAVE, video);
//                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
//            }
//            @Override
//            public void onSuccess(Object o) {
//                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_VIDEO_SAVE, video);
//            }
//        });
//    }

//    private void saveLikeToRedis(Like like) {
//        // 消息队列，同步更改至Redis
//        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_LIKE_SAVE, like.getVideoId().toString(), JSON.toJSONString(like)).addCallback(new ListenableFutureCallback() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_LIKE_SAVE, like);
//                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
//            }
//            @Override
//            public void onSuccess(Object o) {
//                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_LIKE_SAVE, like);
//            }
//        });
//    }

    @Transactional
    public void like(LikeDTO likeDTO) {
        likeDTO.setUserId(BaseContext.getCurrentId());
        // 消息队列，异步点赞
        kafkaTemplateWithLatency.send(MessageConstant.KAFKA_TOPIC_LIKE_SAVE, likeDTO.getVideoId().toString(), JSON.toJSONString(likeDTO)).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_LIKE_SAVE, likeDTO);
                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
            }
            @Override
            public void onSuccess(Object o) {
                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_LIKE_SAVE, likeDTO);
            }
        });
    }
}
