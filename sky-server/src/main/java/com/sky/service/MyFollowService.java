package com.sky.service;

import com.sky.dto.MyFollowRequestDTO;
import com.sky.dto.MyUserLoginDTO;
import com.sky.entity.MyUser;

public interface MyFollowService {

    public void addFollow(MyFollowRequestDTO followRequest);

    public void deleteFollow(MyFollowRequestDTO followRequest);
}
