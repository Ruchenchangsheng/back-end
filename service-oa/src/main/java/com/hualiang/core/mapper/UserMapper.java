package com.hualiang.core.mapper;

import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hualiang.model.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface UserMapper extends BaseMapper<User> {
    
    <T> User selectWithRoleByField(@Param("field") String field, @Param("value") T value);
    
    IPage<User> selectAllWithRole(@Param("page") IPage<User> page, @Param("map") Map<String, String> map);
}