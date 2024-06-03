package com.sky.controller.admin;

import com.sky.dto.MyFollowRequestDTO;
import com.sky.result.Result;
import com.sky.service.MyFollowService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
