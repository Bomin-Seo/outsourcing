package com.sparta.outsourcing.dto;

import lombok.Getter;

@Getter
public class RestaurantDto {

    private final String restaurantName;
    private final String restaurantInfo;
    private final String phoneNumber;
    private final Long likes;

    public RestaurantDto(String restaurantName, String restaurantInfo, String phoneNumber, Long likes) {
        this.restaurantName = restaurantName;
        this.restaurantInfo = restaurantInfo;
        this.phoneNumber = phoneNumber;
        this.likes = likes;
    }

}
