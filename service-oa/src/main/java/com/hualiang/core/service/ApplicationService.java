package com.hualiang.core.service;

import java.util.List;
import java.util.Map;

import com.hualiang.model.Facility;
import com.hualiang.model.HotelApplication;
import com.hualiang.model.RoomType;

public interface ApplicationService {

    Integer submit(Integer hotelId, String title, String message, Object data);
    
    String review(Integer id, Boolean status);

    List<HotelApplication> getPendingApplication(String option, String value);

    List<HotelApplication> searchApplication(Map<String, String> option);
    
    Integer addFacility(Facility facility);
    
    Integer deleteFacility(Integer facilityId);
    
    Integer updateFacility(Facility facility);

    Facility getFacilityById(Integer facilityId);

    List<Facility> getAllFacility(String facilityName, Integer hotelId);
    
    List<String> getAllFacilityByRoom(Integer roomId);
    
    Integer upadteFacilityToRoom(List<Integer> facilities, Integer roomId);

    List<String> getRoomTypeName(Integer hotelId);

    Integer addType(RoomType roomType);

    Integer deleteType(Integer typeId);

    Integer updateType(RoomType roomType);

    List<RoomType> getAllRoomType(String typeName, Integer hotelId);

    RoomType getRoomType(Integer typeId);
}
