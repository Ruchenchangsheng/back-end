package com.hualiang.model;

import lombok.Data;

@Data
public class RoomDetail {
    private int roomId;
    private int userId;
    private int hotelId;
    private String typeName;
    private int size;
    private double price;
    private String status;
    private Integer typeId;
    private String message;
    private String photo;
    private int facilityId;
    private String facilityNames;
    private String facilityDescriptions;
    private int numOfGuests;
}
