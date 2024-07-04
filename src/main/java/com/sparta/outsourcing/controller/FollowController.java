package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.RestaurantDto;
import com.sparta.outsourcing.dto.RestaurantResponseDto;
import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.repository.FollowerRestaurantSearCond;
import com.sparta.outsourcing.security.UserDetailsImpl;
import com.sparta.outsourcing.service.LikeService;
import com.sparta.outsourcing.service.RestaurantService;
import com.sparta.outsourcing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final UserService userService;
    private final LikeService likeService;
    private final RestaurantService restaurantService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> followUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.followUser(userDetails.getUser() ,userId);
    }

    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<String> unfollowUser(@PathVariable("userId") Long userId,  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.unfollowUser(userId, userDetails.getUser());
    }

    @GetMapping("/follower/{userId}")
    public ResponseEntity<List<RestaurantDto>> getFollowerRestaurants(@PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        List<RestaurantDto> restaurants = likeService.getFollowedUsersRestaurants(userId, page, size);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/filtered")
    public ResponseEntity<Page<RestaurantResponseDto>> getFilteredPosts(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "admin") UserRoleEnum role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        FollowerRestaurantSearCond cond = new FollowerRestaurantSearCond(role, author);
        Page<RestaurantResponseDto> restaurants = restaurantService.getFilteredRestaurants(cond, page, size);
        return ResponseEntity.ok(restaurants);
    }
}
