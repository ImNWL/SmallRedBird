package com.sky.controller.admin;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.*;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.service.VideoService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/SRB/video")
@Api(tags = "视频相关接口")
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public Result uploadVideo(VideoUploadDTO videoUploadDTO) {
        log.info("文件上传 {}", videoUploadDTO);
        videoService.upload(videoUploadDTO);
        return Result.success();
    }

    @PostMapping("/like")
    public Result like(@RequestBody LikeDTO likeDTO) {
        log.info("点赞 {}", likeDTO);
        videoService.like(likeDTO);
        return Result.success();
    }
}
