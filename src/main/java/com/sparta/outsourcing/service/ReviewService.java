package com.sparta.outsourcing.service;

import com.sparta.outsourcing.dto.ReviewDto;
import com.sparta.outsourcing.entity.Order;
import com.sparta.outsourcing.entity.Restaurant;
import com.sparta.outsourcing.entity.Review;
import com.sparta.outsourcing.entity.User;
import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.exception.InvalidAccessException;
import com.sparta.outsourcing.repository.OrderRepository;
import com.sparta.outsourcing.repository.ReviewRepository;
import com.sparta.outsourcing.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final MessageSource messageSource;

    @Autowired
    public ReviewService(OrderRepository orderRepository, ReviewRepository reviewRepository, MessageSource messageSource) {
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.messageSource = messageSource;
    }

    public ResponseEntity<String> addReview(ReviewDto reviewDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        Optional<Order> optionalOrder = orderRepository.findById(reviewDto.getOrderId());
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (order.getUser().getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ROLE_ADMIN)) {
                Review review = new Review(user, order, reviewDto.getContent(), order.getRestaurant());
                reviewRepository.save(review);
                return ResponseEntity.ok("리뷰가 성공적으로 작성되었습니다.");
            } else {
                throw new InvalidAccessException(messageSource.getMessage(
                        "invalid.access", null, "적합하지 않은 접근입니다.", Locale.getDefault()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주문 정보가 존재하지 않습니다.");
        }
    }

    public ResponseEntity<ReviewDto> getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("해당 리뷰가 존재하지 않습니다."));
        ReviewDto reviewDto = convertToDto(review);
        return ResponseEntity.status(HttpStatus.OK).body(reviewDto);
    }

    public ResponseEntity<List<ReviewDto>> getAllReviews(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        List<ReviewDto> reviewDtoList = reviewPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviewDtoList);
    }

    private ReviewDto convertToDto(Review review) {
        return new ReviewDto(review.getOrder().getOrderId(), review.getContent(), review.getLikes());
    }

    public ResponseEntity<String> updateReview(Long reviewId, ReviewDto reviewDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();

            if (review.getUser().getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ROLE_ADMIN)) {
                review.update(reviewDto.getContent());
                reviewRepository.save(review);
                return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(403).body("수정할 권한이 없습니다.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<String> deleteReview(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            if (review.getUser().getUsername().equals(user.getUsername()) || user.getRole().equals(UserRoleEnum.ROLE_ADMIN)) {
                reviewRepository.delete(review);
                return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(403).body("삭제할 권한이 없습니다.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}