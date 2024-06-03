package com.sky.service;

import com.sky.dto.MyUserLoginDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.UserRegisterDTO;
import com.sky.dto.UserUpdateDTO;
import com.sky.entity.MyUser;
import com.sky.entity.User;

public interface UserService {

    User login(UserLoginDTO userLoginDTO);

    void register(UserRegisterDTO userRegisterDTO);

    boolean checkUserExists(String phone);

    void update(UserUpdateDTO userUpdateDTO);
}
