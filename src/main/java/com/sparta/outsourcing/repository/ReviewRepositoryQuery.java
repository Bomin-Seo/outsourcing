package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryQuery {
    Page<Review> findLikedReviewsByUserId(Long userId, Pageable pageable);
}
