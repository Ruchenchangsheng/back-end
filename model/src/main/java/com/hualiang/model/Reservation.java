package com.hualiang.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hualiang.model.groups.UpdateGroup;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reservation {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TableField(exist = false)
    private String hotelName;

    @NotNull(message = "订单ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotNull(message = "房间ID不能为空")
    private Integer roomId;

    @NotNull(message = "酒店ID不能为空")
    private Integer hotelId;

    private Timestamp createTime = new Timestamp(System.currentTimeMillis());

    private Timestamp checkinTime;

    private Timestamp checkoutTime;

    private Timestamp startDate;
    private Timestamp endDate;

    private Boolean status;

    public String createTimeToString() {
        return df.format(createTime);
    }

    public String checkinTimeToString() {
        return df.format(checkinTime);
    }

    public String checkoutTimeToString() {
        return checkoutTime != null ? df.format(checkoutTime) : null;
    }
}