package com.sky.mapper;

import com.sky.entity.Like;
import com.sky.entity.UserLike;
import com.sky.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper {
    void insert(Like like);

    void insertUser(UserLike userLike);

    @Select("select * from tb_like where video_id = #{videoId}")
    Like select(Long videoId);

    @Select("select * from tb_user_like where user_id = #{userId}")
    UserLike selectUser(Long userId);

    void update(Like like);

    void updateUser(UserLike userLike);
}
