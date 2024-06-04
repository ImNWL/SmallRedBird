package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.User;

public interface VideoService {

    void upload(VideoUploadDTO videoUploadDTO);

    void like(LikeDTO likeDTO);
}
