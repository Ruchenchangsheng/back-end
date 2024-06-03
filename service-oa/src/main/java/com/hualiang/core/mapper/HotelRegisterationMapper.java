package com.hualiang.core.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualiang.model.HotelRegisteration;

public interface HotelRegisterationMapper extends BaseMapper<HotelRegisteration> {
    // 获取所有预定记录
    List<HotelRegisteration> getAllHotelRegisteration(Map<String,String> option);
}