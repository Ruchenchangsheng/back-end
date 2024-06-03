package com.hualiang.core.service;

import com.hualiang.model.Reservation;
import com.hualiang.model.ReservationDetail;

import java.util.List;

public interface ReservationService {

    // IPage<Reservation> getReservationPage(Map<String,String> option, Integer pageNum);
    
    Integer update(Reservation reservation);

//    List<Integer> getReservationsByUid(Integer uid);

    // IPage<Reservation> getAll();

    //mobile
    List<Reservation> getUserReservationsByUid(Integer uid);

    List<ReservationDetail> getUserReservationDetailsByUid(Integer orderId);

}
