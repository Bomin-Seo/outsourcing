package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.entity.User;

import java.util.Optional;

public interface UserRepositoryQuery {

    Optional<User> findByUsername(String username);
    Optional<User> findByRefreshtoken(String refreshToken);
}
