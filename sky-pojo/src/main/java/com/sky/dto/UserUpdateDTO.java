package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {

    private Long id;

    private String phone;

    private String username;

    private String password;

    private String salt;

    private String image;

    private String signature;

    private Integer status;

    private Integer attention;

    private Integer followers;

    private Integer works;

    private Integer likes;

}
