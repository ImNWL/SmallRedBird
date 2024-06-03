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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户id,采用数据库自增
     */
    private Long id;
    /**
     * 手机号，唯一
     */
    private String phone;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    /**
     * 盐
     */
    private String salt;

    /**
     * 用户头像url
     */
    private String image;

    /**
     * 用户个性签名
     */
    private String signature;

    /**
     * 用户状态，0 正常，1 禁用
     */
    private Integer status;

    /**
     * 用户关注数
     */
    private Integer attention;

    /**
     * 用户粉丝数
     */
    private Integer followers;

    /**
     * 用户作品数
     */
    private Integer works;

    /**
     * 用户获赞数
     */
    private Integer likes;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

