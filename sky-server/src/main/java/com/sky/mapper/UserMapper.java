package com.sky.mapper;

import com.sky.entity.MyUser;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where phone = #{phone}")
    User getByPhone(String phone);

    @Insert("insert into tb_user (phone, username, password) VALUES (#{phone},#{username},#{password})")
    void insert(User user);

    void updateById(User user);
}
