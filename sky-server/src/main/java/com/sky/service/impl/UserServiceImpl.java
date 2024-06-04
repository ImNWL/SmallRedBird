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

    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        // Todo 布隆过滤器

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

            } finally {
                // 释放锁
                redisLockCompoent.releaseLock(lockKey, lockValue);
            }
        } else {
            throw new BadRegisterException(MessageConstant.TRY_LATER);
        }
    }

    public User login(UserLoginDTO userLoginDTO) {
        // Todo 布隆过滤器

        User user = getUserByPhoneWithCache(userLoginDTO.getPhone());
        if (user == null) {
            // 用户不存在
            throw new BadRegisterException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!userLoginDTO.getPassword().equals(user.getPassword())) {
            //密码错误
            throw new BadRegisterException(MessageConstant.PASSWORD_ERROR);
        }

        // 返回实体对象
        return user;
    }

    @Transactional
    public User getUserByPhoneWithCache(String phone) {
        User user = (User) redisTemplate.opsForValue().get("user:" + phone);
        if (user == null) {
            log.info("用户不存在Redis缓存中");
            // 执行数据库操作
            user = userMapper.getByPhone(phone);
            if (user == null) {
                log.info("用户不存在数据库中");
                // 账号不存在
                return null;
            }
            // 同步至Redis
            log.info("更新至Redis缓存: {}", user);
            redisTemplate.opsForValue().set("user:" + phone, user, 10, TimeUnit.SECONDS);
        } else {
            log.info("用户存在Redis缓存中");
            // 更新Redis缓存过期时间
            redisTemplate.expire("user:" + phone, 10, TimeUnit.SECONDS);
        }
        return user;
    }

    @Transactional
    public void updateByPhone(UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO .getPhone() == null) {
            throw new BadRegisterException(MessageConstant.ERROR);
        }
        User user = new User();
        // 对象属性拷贝
        BeanUtils.copyProperties(userUpdateDTO, user);

        // 执行数据库操作
        userMapper.updateByPhone(user);

        // 消息队列，同步更改至Redis
        deleteInRedis(user);
    }


    private void deleteInRedis(User user) {
        // 消息队列，同步更改至Redis
        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_USER_DELETE, user.getPhone(),"user:" + user.getPhone()).addCallback(new ListenableFutureCallback() {
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



//    private void saveToRedis(User user) {
//        // 消息队列，同步更改至Redis
//        kafkaTemplate.send(MessageConstant.KAFKA_TOPIC_USER_REGISTER, JSON.toJSONString(user)).addCallback(new ListenableFutureCallback() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.error("发送消息队列失败至{}: {}", MessageConstant.KAFKA_TOPIC_USER_REGISTER, user);
//                throw new BadRegisterException(MessageConstant.KAFKA_PROBLEM);
//            }
//            @Override
//            public void onSuccess(Object o) {
//                log.info("发送消息队列成功至{}: {}", MessageConstant.KAFKA_TOPIC_USER_REGISTER, user);
//            }
//        });
//    }

//    private boolean checkUserExists(String phone) {
//        if (redisTemplate.opsForValue().get("user:" + phone) != null) {
//            log.info("用户已存在");
//            return true;
//        }
//        log.info("用户不存在");
//        return false;
//    }
}
