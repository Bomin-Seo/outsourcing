package com.sparta.outsourcing.dto;

import com.sparta.outsourcing.entity.Order;
import com.sparta.outsourcing.entity.User;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResponseDto {
    private final String userName;
    private final int totalPrice;
    private final List<String> menuList;

    public OrderResponseDto(String userName, List<String> menuList, int totalPrice) {
        this.userName = userName;
        this.menuList = menuList;
        this.totalPrice = totalPrice;
    }

    public static OrderResponseDto toDto(Order order) {
        return new OrderResponseDto(
                order.getUser().getUsername(),
                order.getMenuList(),
                order.getTotalPrice()
        );
    }
}
