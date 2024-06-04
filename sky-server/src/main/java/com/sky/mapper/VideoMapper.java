package com.sky.mapper;

import com.sky.entity.User;
import com.sky.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VideoMapper {
    void insert(Video video);
}
