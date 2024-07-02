package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.entity.Restaurant;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantRepositoryQuery {
    List<Restaurant> findLikedRestaurantsByUserId(Long userId, Pageable pageable);

    List<Restaurant> findRestaurantsByFollowedUsers(Long userId, Pageable pageable);
}
