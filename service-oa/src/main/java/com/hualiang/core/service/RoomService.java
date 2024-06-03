package com.hualiang.core.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hualiang.model.Facility;
import com.hualiang.model.Reservation;
import com.hualiang.model.Room;
import com.hualiang.model.RoomDetail;

public interface RoomService extends IService<Room> {
    
    // @Deprecated
    Integer addRoom(Room room);
    
    IPage<Room> getAll();
    
    IPage<Room> getRoomPage(Map<String,String> option, Integer pageNum);
    
    Integer updateRoomStatus(Integer roomId, Integer status, Integer userId);

    Integer reserve(Reservation reservation);
    
    IPage<Reservation> getPendingReservation();

    List<Reservation> getReservationByMID(Integer mid);

    IPage<Reservation> searchReservation(Map<String,String> option);

    List<Integer> getPendingRoomIdByUID(Integer uid);

    public Integer deleteCheckinRecord(Integer id);

    Integer review(Integer id, Boolean status);

	List<String> getRoomTypeName(Integer hotelId);


    //Mobile
    List<Room> searchRoomsByHotelId(int hotelId);
    List<RoomDetail> searchRoomDetail(int roomId);
//    List<Facility> searchFacility(int roomId);
}
