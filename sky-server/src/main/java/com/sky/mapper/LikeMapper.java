package com.sky.mapper;

import com.sky.entity.Like;
import com.sky.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper {
    void insert(Like like);

    @Select("select * from tb_like where video_id = #{videoId}")
    Like select(Long videoId);

    void update(Like like);
}
