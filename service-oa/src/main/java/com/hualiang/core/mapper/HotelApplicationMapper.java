package com.hualiang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualiang.model.HotelApplication;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface HotelApplicationMapper extends BaseMapper<HotelApplication> {
    // 获取所有酒店申请
    List<HotelApplication> getAllHotelApplication(@Param("map") Map<String,String> option);
}