package com.sparta.outsourcing.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.outsourcing.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryQuery {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Restaurant> findLikedRestaurantsByUserId(Long userId, Pageable pageable) {
        QUser user = QUser.user;
        QRestaurantLike restaurantLike = QRestaurantLike.restaurantLike;
        QRestaurant restaurant = QRestaurant.restaurant;

        return jpaQueryFactory.select(restaurant)
                .from(user)
                .leftJoin(user.restaurantLikes, restaurantLike)
                .leftJoin(restaurantLike.restaurant, restaurant)
                .where(user.id.eq(userId)
                        .and(restaurantLike.Liked.isTrue()))
                .orderBy(restaurant.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<Restaurant> findRestaurantsByFollowedUsers(Long userId, Pageable pageable) {
        QUser user = QUser.user;
        QRestaurant restaurant = QRestaurant.restaurant;
        QUser followedUser = new QUser("followedUser");

        return jpaQueryFactory.selectFrom(restaurant)
                .leftJoin(restaurant.user, followedUser)
                .leftJoin(followedUser.followers, user)
                .where(user.id.eq(userId))
                .orderBy(restaurant.createdAt.desc(), followedUser.username.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public Page<Restaurant> filteringRestaurant(FollowerRestaurantSearCond cond, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;
        QUser user = QUser.user;

        var query = jpaQueryFactory.selectFrom(restaurant)
                .leftJoin(restaurant.user, user)
                .where(user.role.eq(cond.getRole())
                        .and(user.username.eq(cond.getAuthor())))
                .orderBy(restaurant.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Restaurant> restaurants = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(restaurants, pageable, total);
    }

}
