package com.sky.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long followerId; // 关注者ID

    private Long followedId; // 被关注者ID

}
