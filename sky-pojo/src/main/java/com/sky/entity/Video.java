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
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 视频id
     */
    private Long id;

    /**
     * 视频作者id
     */
    private Long authorId;

    /**
     * 视频简介
     */
    private String title;

    /**
     * 分区，0为热门，其他待定
     */
    private Integer section;

    /**
     * 视频url
     */
    private String videoUrl;

    /**
     * 视频状态。0正常    1删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 点赞数
     */
    private Long likes;

    /**
     * 收藏数
     */
    private Long collects;

    /**
     * 评论数
     */
    private Long comments;
}

