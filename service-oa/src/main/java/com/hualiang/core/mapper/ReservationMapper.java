package com.hualiang.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hualiang.model.Reservation;

import java.util.List;
import java.util.Map;

import com.hualiang.model.ReservationDetail;
import org.apache.ibatis.annotations.Param;

public interface ReservationMapper extends BaseMapper<Reservation> {
    // 获取所有预定记录
    IPage<Reservation> getAllReservation(@Param("page") IPage<Reservation> page, @Param("map") Map<String,String> map);
    
    // 根据经营者ID获取预定记录
    List<Reservation> getReservationByMID(Integer mid);
    
    // 根据UID获取预定记录ID
    List<Integer> getPendingRoomIdByUID(Integer uid);


    //mobile
    List<Reservation> getUserReservationByUID(Integer uid);

    List<ReservationDetail> getDetail(Integer orderId);
}