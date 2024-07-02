package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.OrderRequestDto;
import com.sparta.outsourcing.dto.OrderResponseDto;
import com.sparta.outsourcing.security.UserDetailsImpl;
import com.sparta.outsourcing.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    // 주문 등록
    @PostMapping("/{restaurantId}")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable("restaurantId") Long restaurantId ,@RequestBody List<OrderRequestDto> menuList, @AuthenticationPrincipal UserDetailsImpl authentication) {
        OrderResponseDto responseDto = orderService.createOrder(restaurantId, menuList, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    // 모든 주문 조회
    // 페이지 네이션 5개씩 생성일자 기준 최신순
    @GetMapping("/")
    public Page<OrderResponseDto> getOrders(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size,
                                            @RequestParam(defaultValue = "createdAt") String sortBy) {
        return orderService.getOrders(page, size, sortBy);
    }
    // 특정 주문 조회
    @GetMapping("/{orderId}")
    public List<OrderResponseDto> getOrder(@PathVariable long orderId) {
        return orderService.getOrder(orderId);
    }

    // 주문 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable long orderId, @RequestBody List<OrderRequestDto> menuList, @AuthenticationPrincipal UserDetailsImpl authentication) {
        OrderResponseDto responseDto = orderService.updateOrder(orderId, menuList, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable long orderId, @AuthenticationPrincipal UserDetailsImpl authentication) {
        orderService.deleteOrder(orderId, authentication);
        return new ResponseEntity<String>("주문 삭제", HttpStatus.OK);
    }
}