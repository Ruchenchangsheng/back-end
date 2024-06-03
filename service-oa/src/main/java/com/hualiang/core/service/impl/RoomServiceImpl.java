package com.hualiang.core.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hualiang.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualiang.core.mapper.ReservationMapper;
import com.hualiang.core.mapper.RoomMapper;
import com.hualiang.core.service.RoomService;

@Service
@Transactional
public class RoomServiceImpl extends ServiceImpl<RoomMapper,Room> implements RoomService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public Integer addRoom(Room room) {
        int result = baseMapper.insert(room);
        return result > 0 ? room.getRoomId() : null;
    }


    @Override
    public IPage<Room> getAll() {
        Map<String, String> map = new HashMap<>();
        map.put("option", "room_id");
        map.put("value", "");
        return baseMapper.getAllRoom(new Page<Room>(1, 5).setSize(-1), map);
    }

    @Override
    public IPage<Room> getRoomPage(Map<String, String> option, Integer pageNum) {
//        return baseMapper.getAllRoom(new Page<Room>(pageNum, 5), option);
        return baseMapper.getAllRoom(new Page<Room>(pageNum, 5), option);
    }

    @Override
    public Integer updateRoomStatus(Integer roomId, Integer status, Integer userId) {
        int result = 1;
        Room room = baseMapper.getRoomByField("room_id", roomId);
        QueryWrapper<Reservation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId).eq("status", 1).and(wrapper -> wrapper.isNull("checkin_time").or().isNull("checkout_time"));
        Reservation reservation = reservationMapper.selectOne(queryWrapper);
        UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
        if (reservation == null) {
            updateWrapper.eq("room_id", roomId).set("status", status).set("user_id", userId);
            return baseMapper.update(null, updateWrapper) * result;
        }
        /**
         * 1 - 2：入住，更新记录入住时间
         * 2 - 0：退房，更新记录退房时间
         * 1 - 0：取消预订，删除记录
         */
        if (room.getStatus() == 1) {
            if (status == 2) { // 入住
                Timestamp checkinTime = new Timestamp(System.currentTimeMillis());
                result = reservationMapper.update(null, new UpdateWrapper<Reservation>().eq("id", reservation.getId()).set("checkin_time", checkinTime));
            } else { // 取消预订
                result = reservationMapper.deleteById(reservation.getId());
            }
        } else if (room.getStatus() == 2 && status == 0) { // 退房
            Timestamp checkoutTime = new Timestamp(System.currentTimeMillis());
            result = reservationMapper.update(null, new UpdateWrapper<Reservation>().eq("id", reservation.getId()).set("checkout_time", checkoutTime));
        }
        updateWrapper.eq("room_id", roomId).set("status", status).set("user_id", status == 0 ? null : reservation.getUserId());
        return baseMapper.update(null, updateWrapper) * result;
    }

    @Override
    public Integer reserve(Reservation reservation) {
        return reservationMapper.insert(reservation) > 0 ? reservation.getId() : null;
    }

    @Override
    public IPage<Reservation> getPendingReservation() {
        Map<String, String> map = new HashMap<>();
        map.put("option", "status");
        map.put("value", "null");
        return reservationMapper.getAllReservation(new Page<Reservation>(1, 5).setSize(-1), map);
    }

    @Override
    public IPage<Reservation> searchReservation(Map<String, String> option) {
        return reservationMapper.getAllReservation(new Page<Reservation>(1, 5).setSize(-1), option);
    }
    
    @Override
    public List<Integer> getPendingRoomIdByUID(Integer uid) {
        return reservationMapper.getPendingRoomIdByUID(uid);
    }
    
    @Override
    public List<Reservation> getReservationByMID(Integer mid) {
        return reservationMapper.getReservationByMID(mid);
    }
    
    @Override
    public Integer deleteCheckinRecord(Integer id) {
        return reservationMapper.deleteById(id);
    }

    @Override
    public Integer review(Integer id, Boolean status) {
        int result = 1;
        UpdateWrapper<Room> updateWrapper = new UpdateWrapper<>();
        Reservation reservation = reservationMapper.selectById(id);
        if (status == null) { // 取消预订，删除记录
            updateWrapper.eq("room_id", reservation.getRoomId()).set("status", 0).set("user_id", null);
            result = baseMapper.update(null, updateWrapper);
//            return reservationMapper.deleteById(id);
        }
        if (status) { // 审核通过，更新房间状态
            updateWrapper.eq("room_id", reservation.getRoomId()).set("status", 1).set("user_id", reservation.getUserId());
            result = baseMapper.update(null, updateWrapper);
        }
        return reservationMapper.update(null, new UpdateWrapper<Reservation>().eq("id", id).set("status", status)) * result;
    }

	@Override
	public List<String> getRoomTypeName(Integer hotelId) {
		return baseMapper.getAllRoomTypeNameByHotel(hotelId);
	}


    //Mobile

    @Autowired
    private RoomMapper roomMapper;
    @Override
    public List<Room> searchRoomsByHotelId(int hotelId) {
        List<Room> rooms = roomMapper.searchRoomsByHotelId(hotelId);
        return rooms;
    }

    @Override
    public List<RoomDetail> searchRoomDetail(int roomId) {
        List<RoomDetail> roomDetails = roomMapper.searchRoomDetail(roomId);
        return roomDetails;
    }

//    @Override
//    public List<Facility> searchFacility(int roomId) {
//        List<Facility> roomFacility = roomMapper.searchRoomFacility(roomId);
//        return roomFacility;
//    }
}
