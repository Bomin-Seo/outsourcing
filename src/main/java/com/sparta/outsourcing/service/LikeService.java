package com.sparta.outsourcing.service;

import com.sparta.outsourcing.dto.LikeResponseDto;
import com.sparta.outsourcing.dto.RestaurantDto;
import com.sparta.outsourcing.dto.ReviewDto;
import com.sparta.outsourcing.entity.*;
import com.sparta.outsourcing.exception.LikeSelfException;
import com.sparta.outsourcing.exception.UserNotFoundException;
import com.sparta.outsourcing.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public LikeResponseDto updateRestaurantLike(Long contentId, User user) {
        Restaurant restaurant = restaurantRepository.findById(contentId).orElseThrow(() -> new RuntimeException("식당이 존재하지 않습니다."));

        if (user.getUsername().equals(restaurant.getUser().getUsername())) {
            throw new LikeSelfException(messageSource.getMessage(
                    "like.self", null, "본인이 작성한 컨텐츠에는 좋아요를 등록할 수 없습니다.", Locale.getDefault()
            ));
        }
        RestaurantLike restaurantLike = restaurantLikeRepository.findByUserAndRestaurant(user, restaurant)
                .orElseGet(() -> new RestaurantLike(user, restaurant));
        restaurantLike.update();
        restaurantLikeRepository.save(restaurantLike);
        return calculateRestaurantlike(restaurantLike, restaurant);
    }

    public LikeResponseDto updateReviewLike(Long contentId, User user) {
        Review review = reviewRepository.findById(contentId).orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));

        if (user.getUsername().equals(review.getUser().getUsername())) {
            throw new LikeSelfException(messageSource.getMessage(
                    "like.self", null, "본인이 작성한 컨텐츠에는 좋아요를 등록할 수 없습니다.", Locale.getDefault()
            ));
        }
        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseGet(() -> new ReviewLike(user, review));
        reviewLike.update();
        reviewLikeRepository.save(reviewLike);
        return calculateReviewlike(reviewLike, review);
    }

    private LikeResponseDto calculateRestaurantlike(RestaurantLike restaurantLike, Restaurant restaurant) {
        Long cnt =  restaurant.updateLike(restaurantLike.isLiked());
        return new LikeResponseDto(restaurantLike.isLiked(), cnt);
    }

    public LikeResponseDto calculateReviewlike(ReviewLike reviewLike, Review review) {
        Long cnt =  review.updateLike(reviewLike.isLiked());
        return new LikeResponseDto(reviewLike.isLiked(), cnt);
    }

    public List<RestaurantDto> getLikedRestaurant(Long userId, int page, int size, String sortBy) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("해당 유저가 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("username").descending());

        List<Restaurant> followRestaurants = restaurantRepository.findLikedRestaurantsByUserId(userId, pageable);

        return followRestaurants.stream()
                .map(restaurant -> new RestaurantDto(
                        restaurant.getRestaurantName(),
                        restaurant.getRestaurantInfo(),
                        restaurant.getPhoneNumber(),
                        restaurant.getLikes()
                ))
                .collect(Collectors.toList());
    }

    public Page<ReviewDto> getLikedReviews(Long userId, int page, int size, String sortBy) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Review> likedReviewsPage = reviewRepository.findLikedReviewsByUserId(userId, pageable);

        return ReviewDto.mapFromPage(likedReviewsPage);
    }

    public List<RestaurantDto> getFollowedUsersRestaurants(Long userId, int page, int size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());

        List<Restaurant> restaurants = restaurantRepository.findRestaurantsByFollowedUsers(userId, pageable);

        return restaurants.stream()
                .map(restaurant -> new RestaurantDto(
                        restaurant.getRestaurantName(),
                        restaurant.getRestaurantInfo(),
                        restaurant.getPhoneNumber(),
                        restaurant.getLikes()
                ))
                .collect(Collectors.toList());
    }
}