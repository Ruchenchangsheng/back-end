package com.hualiang.core.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hualiang.core.mapper.FacilityMapper;
import com.hualiang.core.mapper.HotelApplicationMapper;
import com.hualiang.core.mapper.RoomTypeMapper;
import com.hualiang.core.service.ApplicationService;
import com.hualiang.model.Facility;
import com.hualiang.model.HotelApplication;
import com.hualiang.model.RoomType;
import com.hualiang.model.util.FileUtils;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private FacilityMapper facilityMapper;
    @Autowired
    private RoomTypeMapper roomTypeMapper;
    @Autowired
    private HotelApplicationMapper hotelApplicationMapper;

    @Override
    public Integer submit(Integer hotelId, String title, String message, Object data) {
        title += data instanceof Facility ? "酒店设施" : "酒店房型";
        HotelApplication hotelApplication = new HotelApplication(hotelId, title, data);
        return hotelApplicationMapper.insert(hotelApplication) > 0 ? hotelApplication.getId() : null;
    }

    @Override
    public String review(Integer id, Boolean status) {
        Integer result = 1; String IDString = "";
        HotelApplication hotelApplication = hotelApplicationMapper.selectById(id);
        String type = hotelApplication.getTitle().substring(4);
        if (status) {
            switch (type) {
                case "设施":
                    Facility facility = hotelApplication.getObject(Facility.class);
                    result = facility.getFacilityId() == null ? facilityMapper.insert(facility) : facilityMapper.updateById(facility);
                    IDString = "facility:" + facility.getFacilityId().toString();
                    break;
                case "房型":
                    RoomType roomType = hotelApplication.getObject(RoomType.class);
                    if (roomType.getTypeId() == null) {
                        result = roomTypeMapper.insert(roomType);
                    } else {
                        String oldPath = roomTypeMapper.selectById(roomType.getTypeId()).toPath();
                        result = roomTypeMapper.updateById(roomType);
                        if (result > 0) { FileUtils.deleteDir(oldPath); }
                    }
                    IDString = "type:" + roomType.getTypeId().toString();
                    break;
            }
        } else if (type.equals("房型")) {
            FileUtils.deleteDir(hotelApplication.getMap().get("photo").split("\\|", 2)[0]);
        }
        Timestamp reviewTime = new Timestamp(System.currentTimeMillis());
        return hotelApplicationMapper.update(null, new UpdateWrapper<HotelApplication>().eq("id", id).set("status", status).set("review_time", reviewTime)) * result > 0 ? IDString : null;
    }

    @Override
    public List<HotelApplication> getPendingApplication(String option, String value) {
        Map<String, String> map = new HashMap<>();
        map.put("option", option);
        map.put("value", value);
        map.put("status", "null");
        return hotelApplicationMapper.getAllHotelApplication(map);
    }

    @Override
    public List<HotelApplication> searchApplication(Map<String, String> option) {
        return hotelApplicationMapper.getAllHotelApplication(option);
    }

    @Override
    public Integer addFacility(Facility facility) {
        return facilityMapper.insert(facility) > 0 ? facility.getFacilityId() : null;
    }

    @Override
    public Integer deleteFacility(Integer facilityId) {
        return facilityMapper.deleteById(facilityId);
    }

    @Override
    public Integer updateFacility(Facility facility) {
        return facilityMapper.updateById(facility) > 0 ? facility.getFacilityId() : null;
    }

    @Override
    public Facility getFacilityById(Integer facilityId) {
        return facilityMapper.selectById(facilityId);
    }

    @Override
    public List<Facility> getAllFacility(String facilityName, Integer hotelId) {
        QueryWrapper<Facility> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id", hotelId).like("facility_name", facilityName);
        return facilityMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> getAllFacilityByRoom(Integer roomId) {
        return facilityMapper.getAllFacilitIdyByRoom(roomId);
    }

    @Override
    public Integer upadteFacilityToRoom(List<Integer> facilities, Integer roomId) {
        facilityMapper.deleteAllFacilityFromRoom(roomId);
        return facilityMapper.insertFacilityToRoom(facilities, roomId);

    }

    @Override
    public List<String> getRoomTypeName(Integer hotelId) {
        return roomTypeMapper.getAllRoomTypeNameByHotel(hotelId);
    }

    @Override
    public Integer addType(RoomType roomType) {
        return roomTypeMapper.insert(roomType) > 0 ? roomType.getTypeId() : null;
    }

    @Override
    public Integer deleteType(Integer typeId) {
        FileUtils.deleteDir(roomTypeMapper.selectById(typeId).toPath());
        return roomTypeMapper.deleteById(typeId);
    }

    @Override
    public Integer updateType(RoomType roomType) {
        return roomTypeMapper.updateById(roomType) > 0 ? roomType.getTypeId() : null;
    }

    @Override
    public List<RoomType> getAllRoomType(String typeName, Integer hotelId) {
        QueryWrapper<RoomType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_id", hotelId).like("type_name", typeName);
        return roomTypeMapper.selectList(queryWrapper);
    }

    @Override
    public RoomType getRoomType(Integer typeId) {
        return roomTypeMapper.selectById(typeId);
    }
}
