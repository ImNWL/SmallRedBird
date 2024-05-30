package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.*;
import com.sky.dto.MyUserLoginDTO;
import com.sky.entity.Employee;
import com.sky.entity.MyUser;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.service.MyFollowService;
import com.sky.service.MyUserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import com.sky.vo.MyUserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

        import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/SRB")
@Api(tags = "我的登陆相关接口")
@Slf4j
public class MyLogController {

    @Autowired
    private MyUserService myUserService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param myUserLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<MyUserLoginVO> login(@RequestBody MyUserLoginDTO myUserLoginDTO) {
        log.info("员工登录：{}", myUserLoginDTO);

        MyUser user = myUserService.login(myUserLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        MyUserLoginVO myUserLoginVO = MyUserLoginVO.builder()
                .id(user.getId())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(myUserLoginVO);
    }
}
