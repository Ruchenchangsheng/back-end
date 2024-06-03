package com.hualiang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hualiang.model.Hotel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {

    // 获取所有酒店
    IPage<Hotel> getAllHotel(@Param("page") IPage<Hotel> page, @Param("map") Map<String, String> map);

    //mobile
    List<Hotel> selectAllHotels();

    List<Hotel> findAvailableHotels(
            @Param("address") String address,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("numOfGuests") int numOfGuests
    );
}