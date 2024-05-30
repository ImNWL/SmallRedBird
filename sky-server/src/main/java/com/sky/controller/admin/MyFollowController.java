package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.MyFollowRequestDTO;
import com.sky.dto.MyUserLoginDTO;
import com.sky.entity.MyUser;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.MyFollowService;
import com.sky.service.MyUserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.MyUserLoginVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/SRB")
@Api(tags = "我的关注相关接口")
@Slf4j
public class MyFollowController {
    @Autowired
    private MyFollowService myFollowService;

    @PostMapping("/follow")
    public Result<String> follow(@RequestBody MyFollowRequestDTO followRequest) {
        myFollowService.addFollow(followRequest);
        return Result.success();
    }

    @PostMapping("/unfollow")
    public Result<String> unfollow(@RequestBody MyFollowRequestDTO followRequest) {
        myFollowService.deleteFollow(followRequest);
        return Result.success();
    }
}
