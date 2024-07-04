package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.LikeResponseDto;
import com.sparta.outsourcing.dto.RestaurantDto;
import com.sparta.outsourcing.dto.ReviewDto;
import com.sparta.outsourcing.enums.ContentTypeEnum;
import com.sparta.outsourcing.exception.LikeSelfException;
import com.sparta.outsourcing.security.UserDetailsImpl;
import com.sparta.outsourcing.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 좋아요 기능 controller
 */
@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PutMapping("/{contentType}/{contentId}")
    public ResponseEntity<String> updateRestaurantLike(@PathVariable("contentType") ContentTypeEnum contentType, @PathVariable("contentId") Long contentId, @AuthenticationPrincipal UserDetailsImpl userDetails) throws LikeSelfException {

        LikeResponseDto likeResponseDto;

        if (contentType.equals(ContentTypeEnum.RESTAURANT)) {
            likeResponseDto = likeService.updateRestaurantLike(contentId, userDetails.getUser());
        } else {
            likeResponseDto = likeService.updateReviewLike(contentId, userDetails.getUser());
        }

        if (likeResponseDto.isLiked()) {
            return ResponseEntity.ok("좋아요를 눌렀습니다! 다른 콘텐츠도 확인해보세요 : " + likeResponseDto.getCnt());
        } else {
            return ResponseEntity.ok("좋아요를 취소했습니다. 다시 좋아요를 누를 수 있습니다 : " + likeResponseDto.getCnt());
        }
    }

    @GetMapping("/{contentType}/{userId}")
    public ResponseEntity<?> getLikedContent(
            @PathVariable("contentType") String contentType,
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "created_at") String sortBy) {

        if (contentType.equalsIgnoreCase("RESTAURANT")) {
            List<RestaurantDto> likedRestaurants = likeService.getLikedRestaurant(userId, page, size, sortBy);
            return ResponseEntity.ok(likedRestaurants);
        } else if (contentType.equalsIgnoreCase("REVIEW")) {
            Page<ReviewDto> likedReviews = likeService.getLikedReviews(userId, page, size, sortBy);
            return ResponseEntity.ok(likedReviews);
        } else {
            return ResponseEntity.badRequest().body("Invalid content type");
        }
    }
}