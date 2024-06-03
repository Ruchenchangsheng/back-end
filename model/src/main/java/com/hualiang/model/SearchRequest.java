package com.hualiang.model;

import lombok.Data;

@Data
public class SearchRequest {
    private String address;
    private String startDate;
    private String endDate;
    private int numOfGuests;
}
