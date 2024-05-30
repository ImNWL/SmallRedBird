package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户关注时传递的数据模型")
public class MyFollowRequestDTO implements Serializable {

    @ApiModelProperty("被关注者ID")
    private Long followedId; // 被关注者ID

}
