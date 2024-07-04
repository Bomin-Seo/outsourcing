package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.enums.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FollowerRestaurantSearCond {
    private UserRoleEnum role;
    private LocalDateTime created_at;
}
