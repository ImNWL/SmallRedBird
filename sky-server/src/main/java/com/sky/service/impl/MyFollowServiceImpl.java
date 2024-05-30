package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.MyFollowRequestDTO;
import com.sky.entity.Dish;
import com.sky.entity.MyFollow;
import com.sky.mapper.MyFollowMapper;
import com.sky.service.MyFollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyFollowServiceImpl implements MyFollowService {
    private static final Logger log = LoggerFactory.getLogger(MyFollowServiceImpl.class);
    @Autowired
    private MyFollowMapper myFollowMapper;

    public void addFollow(MyFollowRequestDTO followRequest) {
        MyFollow follow = new MyFollow();
        BeanUtils.copyProperties(followRequest, follow);
        log.info("id: {}", BaseContext.getCurrentId());
        follow.setFollowerId(BaseContext.getCurrentId());
        myFollowMapper.insert(follow);
    }

    public void deleteFollow(MyFollowRequestDTO followRequest) {
        MyFollow follow = new MyFollow();
        BeanUtils.copyProperties(followRequest, follow);
        log.info("id: {}", BaseContext.getCurrentId());
        follow.setFollowerId(BaseContext.getCurrentId());
        myFollowMapper.delete(follow);
    }
}
