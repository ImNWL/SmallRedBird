package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.sky.component.RedisLockComponent;
import com.sky.constant.MessageConstant;
import com.sky.dto.MyUserLoginDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserRegisterDTO;
import com.sky.dto.UserUpdateDTO;
import com.sky.entity.MyUser;
import com.sky.entity.User;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BadRegisterException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisLockComponent redisLockCompoent;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        // 检查手机号是否已被注册
        if (checkUserExists(userRegisterDTO.getPhone())) {
            throw new BadRegisterException(MessageConstant.PHONE_EXISTS);
        }

        // 分布式锁
        String lockKey = "phone-lock-" + userRegisterDTO.getPhone();
        String lockValue = UUID.randomUUID().toString();
        if (redisLockCompoent.tryLock(lockKey, lockValue, 10, TimeUnit.SECONDS)) {
            try {
                // 执行注册逻辑
                User user = new User();
                // 对象属性拷贝
                BeanUtils.copyProperties(userRegisterDTO, user);
                user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

                // 执行数据库操作
                userMapper.insert(user);

                // 消息队列，同步更改至Redis
                saveToRedis(user);

            } finally {
                // 释放锁
                redisLockCompoent.releaseLock(lockKey, lockValue);
            }
        } else {
            throw new BadRegisterException(MessageConstant.TRY_LATER);
        }
    }

    public boolean checkUserExists(String phone) {
        if (redisTemplate.opsForValue().get("user:" + phone) != null) {
            log.info("用户已存在");
            return true;
        }
        log.info("用户不存在");
        return false;
    }

    @Transactional
    public User login(UserLoginDTO userLoginDTO) {

        // 检查手机号是否已被注册
        // Todo 布隆过滤器
//        if (!checkUserExists(userLoginDTO.getPhone())) {
//            throw new BadRegisterException(MessageConstant.PHONE_NOT_EXISTS);
//        }

        User user = (User) redisTemplate.opsForValue().get("user:" + userLoginDTO.getPhone());
        if (user == null) {
            log.info("用户不存在Redis缓存中");
            // 执行数据库操作
            user = userMapper.getByPhone(userLoginDTO.getPhone());
            if (user == null) {
                log.info("用户不存在数据库中");
                // 账号不存在
                throw new BadRegisterException(MessageConstant.ACCOUNT_NOT_FOUND);
            }
            if (!userLoginDTO.getPassword().equals(user.getPassword())) {
                //密码错误
                throw new BadRegisterException(MessageConstant.PASSWORD_ERROR);
            }
            // 消息队列，同步更改至Redis
            saveToRedis(user);
        } else {
            log.info("用户存在Redis缓存中");
            if (!userLoginDTO.getPassword().equals(user.getPassword())) {
                //密码错误
                throw new BadRegisterException(MessageConstant.PASSWORD_ERROR);
            }
        }

        // 返回实体对象
        return user;
    }

    @Override
    @Transactional
    public void update(UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO .getPhone() == null) {
            throw new BadRegisterException(MessageConstant.ERROR);
        }
        User user = new User();
        // 对象属性拷贝
        BeanUtils.copyProperties(userUpdateDTO, user);

        // 执行数据库操作
        userMapper.updateById(user);

        // 消息队列，同步更改至Redis
        deleteInRedis(user);
    }

    private void saveToRedis(User user) {
        // 消息队列，同步更改至Redis
        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_USER_REGISTER, JSON.toJSONString(user)).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_USER_REGISTER, user);
                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
            }
            @Override
            public void onSuccess(Object o) {
                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_USER_REGISTER, user);
            }
        });
    }

    private void deleteInRedis(User user) {
        // 消息队列，同步更改至Redis
        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_USER_DELETE, JSON.toJSONString(user)).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_USER_DELETE, user);
                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
            }
            @Override
            public void onSuccess(Object o) {
                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_USER_DELETE, user);
            }
        });
    }
}
