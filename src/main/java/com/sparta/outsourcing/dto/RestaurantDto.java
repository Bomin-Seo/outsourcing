package com.sparta.outsourcing.dto;

import com.sparta.outsourcing.entity.Restaurant;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

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

    public static Page<RestaurantDto> mapFromPage(Page<Restaurant> page) {
        List<RestaurantDto> dtos = page.getContent().stream()
                .map(restaurant -> new RestaurantDto(
                        restaurant.getRestaurantName(),
                        restaurant.getRestaurantInfo(),
                        restaurant.getPhoneNumber(),
                        restaurant.getLikes()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }
}
