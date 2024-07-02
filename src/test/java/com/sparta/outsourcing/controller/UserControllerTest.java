package com.sparta.outsourcing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.outsourcing.config.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UserController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)})
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void signUp() {
    }

    @Test
    void getProfile() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void deleteUser() {
    }
}