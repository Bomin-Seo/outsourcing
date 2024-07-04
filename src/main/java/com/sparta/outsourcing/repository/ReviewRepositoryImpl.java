package com.sparta.outsourcing.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.outsourcing.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryQuery {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Review> findLikedReviewsByUserId(Long userId, Pageable pageable) {
        QUser user = QUser.user;
        QReviewLike reviewLike = QReviewLike.reviewLike;
        QReview review = QReview.review;

        List<Review> content = jpaQueryFactory.select(review)
                .from(user)
                .leftJoin(user.reviewLikes, reviewLike)
                .leftJoin(reviewLike.review, review)
                .where(user.id.eq(userId)
                        .and(reviewLike.Liked.isTrue()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(review.count())
                .from(user)
                .leftJoin(user.reviewLikes, reviewLike)
                .leftJoin(reviewLike.review, review)
                .where(user.id.eq(userId)
                        .and(reviewLike.Liked.isTrue()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
