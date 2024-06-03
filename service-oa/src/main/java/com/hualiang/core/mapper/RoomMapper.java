package com.hualiang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hualiang.model.Facility;
import com.hualiang.model.Room;

import java.util.List;
import java.util.Map;

import com.hualiang.model.RoomDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    // 获取所有房间
    IPage<Room> getAllRoom(@Param("page") IPage<Room> page, @Param("map") Map<String, String> map);

    // 根据字段获取房间
    <T> Room getRoomByField(@Param("field") String field, @Param("value") T value);

    // 根据酒店获取房间类型
    List<String> getAllRoomTypeNameByHotel(Integer hotelId);


    //Mobile
    List<Room> searchRoomsByHotelId(@Param("hotelId") int hotelId);
    List<RoomDetail> searchRoomDetail(@Param("roomId") int roomId);
//    List<Facility> searchRoomFacility(@Param("roomId") int roomId);
}