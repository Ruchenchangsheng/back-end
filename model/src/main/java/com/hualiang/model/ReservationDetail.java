package com.hualiang.model;


import lombok.Data;

import java.sql.Timestamp;


@Data
public class ReservationDetail {

    private Integer id;

    private Integer userId;

    private Integer roomId;

    private Integer hotelId;
    private String hotelName;

    private Timestamp createTime;

    private Timestamp checkinTime;

    private Timestamp checkoutTime;

    private Timestamp startDate;
    private Timestamp endDate;

    private Boolean status;

    private String address;
    private Integer price;
    private Integer size;
    private Integer numOfGuests;
    private String type;
}
