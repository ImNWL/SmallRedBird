package com.sky.mapper;

import com.sky.entity.MyFollow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyFollowMapper {
    @Insert("insert into myfollows (follower_id, followed_id) values (#{followerId}, #{followedId})")
    void insert(MyFollow follow);

    @Delete("delete from myfollows where follower_id = #{followerId} AND followed_id = #{followedId};")
    void delete(MyFollow follow);
}
