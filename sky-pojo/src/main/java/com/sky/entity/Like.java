package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 点赞位图
     */
    private byte[] likesBitmap;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

