package com.hualiang.core.service.impl;

import com.hualiang.model.ReservationDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hualiang.core.mapper.ReservationMapper;
import com.hualiang.core.service.ReservationService;
import com.hualiang.model.Reservation;

import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public Integer update(Reservation reservation) {
        UpdateWrapper<Reservation> updateWrapper = new UpdateWrapper<Reservation>();
        updateWrapper.eq("id", reservation.getId()).set("room_id", reservation.getRoomId());
        return reservationMapper.update(null, updateWrapper);
    }

    // @Override
    // public IPage<Reservation> getAll() {
    //     return reservationMapper.getAllReservation(new Page<Reservation>(1, 5).setSize(-1), Map.of("option", "id", "value", ""));
    // }

    // @Override
    // public IPage<Reservation> getReservationPage(Map<String, String> option, Integer pageNum) {
    //     return reservationMapper.getAllReservation(new Page<Reservation>(pageNum, 5), option);
    // }

    @Override
    public List<Reservation> getUserReservationsByUid(Integer uid){
        List<Reservation> reservation = reservationMapper.getUserReservationByUID(uid);
        return  reservation;
    }

    @Override
    public List<ReservationDetail> getUserReservationDetailsByUid(Integer orderId){
        List<ReservationDetail> reservationDetails = reservationMapper.getDetail(orderId);
        return reservationDetails;
    }
}
