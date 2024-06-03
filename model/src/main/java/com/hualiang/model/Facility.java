package com.hualiang.model;

import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Facility {

    @TableId
    @NotNull(message = "设施ID不能为空", groups = UpdateGroup.class)
    private Integer facilityId;

    @NotNull(message = "酒店ID不能为空")
    private Integer hotelId;

    @NotBlank(message = "设施名不能为空")
    private String facilityName;

    @NotBlank(message = "设施描述不能为空")
    private String description;

	public Facility(Map<String,String> map) {
        this.facilityId = map.get("facilityId") != null ? Integer.parseInt(map.get("facilityId")) : null;
        this.hotelId = Integer.parseInt(map.get("hotelId"));
        this.facilityName = map.get("facilityName");
        this.description = map.get("description");
	}

}
