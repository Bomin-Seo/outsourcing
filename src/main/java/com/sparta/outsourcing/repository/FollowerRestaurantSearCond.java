package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.enums.UserRoleEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FollowerRestaurantSearCond {
    private UserRoleEnum role;
    private String author;

    public FollowerRestaurantSearCond(UserRoleEnum role, String author) {
        this.role = role;
        this.author = author;
    }
}
