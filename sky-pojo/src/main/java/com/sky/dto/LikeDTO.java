package com.sky.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

@Data
public class LikeDTO implements Serializable {

    private Long videoId;
    private Long userId;
}
