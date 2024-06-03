package com.hualiang.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hualiang.model.Hotel;
import com.hualiang.model.HotelRegisteration;

public interface HotelService extends IService<Hotel> {

    Integer delete(Integer hotelId);

    IPage<Hotel> getAll(Map<String, String> map);

    IPage<Hotel> getHotelPage(Map<String, String> option, Integer pageNum);

    Integer register(HotelRegisteration hotelRegisteration);

    Integer review(Integer id, Boolean status);

    List<HotelRegisteration> getPendingRegisteration();

    List<HotelRegisteration> searchRegisteration(Map<String, String> option);

    //mobile
    List<Hotel> selectAllHotels();
    List<Hotel> findAvailableHotels(String address, String startDate, String endDate, int numOfGuests);
}
