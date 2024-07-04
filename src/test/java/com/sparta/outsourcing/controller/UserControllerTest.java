package com.sparta.outsourcing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.outsourcing.dto.ProfileDto;
import com.sparta.outsourcing.dto.ProfileResponseDto;
import com.sparta.outsourcing.dto.UserDto;
import com.sparta.outsourcing.entity.User;
import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.exception.AlreadySignupException;
import com.sparta.outsourcing.exception.UserNotFoundException;
import com.sparta.outsourcing.repository.UserRepository;
import com.sparta.outsourcing.security.JwtProvider;
import com.sparta.outsourcing.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {"spring.config.location=classpath:application-test.properties"})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    private UserDto userDto;
    private ProfileDto profileDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Nested
    @Order(1)
    @DisplayName("회원 가입 테스트")
    class signup {
        @BeforeEach
        void setUp() {
            userDto = new UserDto();
            userDto.setUsername("user000");
            userDto.setPassword("Aa123456!");
            userDto.setNickname("nickname");
            userDto.setUserinfo("userinfo");
        }

        @Test
        @DisplayName("회원 가입 성공")
        void signupSuccess() throws Exception {

            // When
            ResultActions resultActions = mvc.perform(post("/api/user/signup/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)));

            // Then
            resultActions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("회원가입 비밀번호 조건 미준수")
        void signupFailure() throws Exception {
            userDto.setPassword("1234");

            ResultActions resultActions = mvc.perform(post("/api/user/signup/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)));

            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @Order(2)
    @DisplayName("프로필 조회 테스트")
    @WithMockUser
    class getProfile {
        @Test
        @DisplayName("프로필 조회 성공")
        void getProfileSuccess() throws Exception {
            ProfileResponseDto profileResponseDto = new ProfileResponseDto("nickname", "userinfo");
            when(userService.getProfile(1L)).thenReturn(ResponseEntity.ok(profileResponseDto));

            ResultActions resultActions = mvc.perform(get("/api/user/1")
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.nickname").value("nickname"))
                    .andExpect(jsonPath("$.userinfo").value("userinfo"));
        }

        @Test
        @DisplayName("프로필 조회 실패 - 유저 없음")
        void getProfileFailure() throws Exception {
            when(userService.getProfile(1L)).thenThrow(new UserNotFoundException("유저를 찾을 수 없습니다."));

            ResultActions resultActions = mvc.perform(get("/api/user/1")
                    .contentType(MediaType.APPLICATION_JSON));

            resultActions.andExpect(status().isBadRequest());
        }
    }

}