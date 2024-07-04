package com.sparta.outsourcing.dto;

import com.sparta.outsourcing.entity.Restaurant;
import com.sparta.outsourcing.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantResponseDto {
    private String restaurantName;
    private String restaurantInfo;
    private String phoneNumber;
    private Long likes;
    private User user;

    public static RestaurantResponseDto createRestaurantResponseDto(Restaurant restaurant) {
        RestaurantResponseDto restaurantResponseDto = new RestaurantResponseDto();
        restaurantResponseDto.setRestaurantName(restaurant.getRestaurantName());
        restaurantResponseDto.setRestaurantInfo(restaurant.getRestaurantInfo());
        restaurantResponseDto.setPhoneNumber(restaurant.getPhoneNumber());
        restaurantResponseDto.setLikes(restaurant.getLikes());
        restaurantResponseDto.setUser(restaurant.getUser());
        return restaurantResponseDto;
    }
}
