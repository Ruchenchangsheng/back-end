package com.hualiang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualiang.model.RoomType;

import java.util.List;

public interface RoomTypeMapper extends BaseMapper<RoomType> {
    
    // 获取所有房间类型名称
    List<String> getAllRoomTypeNameByHotel(Integer hotelId);

}