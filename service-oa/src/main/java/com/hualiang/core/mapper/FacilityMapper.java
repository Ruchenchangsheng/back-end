package com.hualiang.core.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hualiang.model.Facility;

public interface FacilityMapper extends BaseMapper<Facility> {
    // 获取房间内所有设施
    List<String> getAllFacilitIdyByRoom(Integer roomId);

    // 添加设施到房间
    Integer insertFacilityToRoom(@Param("facilities") List<Integer> facilities, @Param("roomId") Integer roomId);

    // 删除房间内设施
    Integer deleteAllFacilityFromRoom(Integer roomId);
}
