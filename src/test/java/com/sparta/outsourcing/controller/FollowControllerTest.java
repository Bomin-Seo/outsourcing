package com.sparta.outsourcing.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = {FollowController.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.class)
class FollowControllerTest {

    @Test
    void followUser() {
    }

    @Test
    void unfollowUser() {
    }

    @Test
    void getFollowerRestaurants() {
    }
}