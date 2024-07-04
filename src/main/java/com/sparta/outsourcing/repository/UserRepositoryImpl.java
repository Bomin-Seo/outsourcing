package com.sparta.outsourcing.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.outsourcing.entity.QUser;
import com.sparta.outsourcing.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryQuery {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> findByUsername(String username) {
        QUser user = QUser.user;
        User result = jpaQueryFactory.selectFrom(user)
                .where(user.username.eq(username))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<User> findByRefreshtoken(String refreshToken) {
        QUser user = QUser.user;
        User result = jpaQueryFactory.selectFrom(user)
                .where(user.refreshtoken.eq(refreshToken))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<User> findFamoususerByFollowers() {
        QUser user = QUser.user;
        return jpaQueryFactory.selectFrom(user)
                .orderBy(user.followers.size().desc())
                .limit(10)
                .fetch();
    }
}
