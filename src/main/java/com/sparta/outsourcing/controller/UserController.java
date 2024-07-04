package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.ProfileDto;
import com.sparta.outsourcing.dto.ProfileResponseDto;
import com.sparta.outsourcing.dto.UserDto;
import com.sparta.outsourcing.exception.SignUpFailureException;
import com.sparta.outsourcing.security.UserDetailsImpl;
import com.sparta.outsourcing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup/{roleId}")
    public ResponseEntity<String> signUp(@PathVariable("roleId") Long roleId, @Valid  @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder errorMessage = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
                errorMessage.append(fieldError.getDefaultMessage());
            }
            throw new SignUpFailureException(errorMessage.toString().trim());
        }
        return userService.signUp(userDto, roleId);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable("userId") Long userId) {
        return userService.getProfile(userId);
    }

    @GetMapping("/top10")
    public List<ProfileResponseDto> getTop10User() {
        return userService.getTop10User();
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateProfile(@PathVariable("userId") Long userId, @Valid @RequestBody ProfileDto profileDto, @AuthenticationPrincipal
    UserDetailsImpl userDetails) {
        return userService.updateProfile(userId, profileDto, userDetails.getUser());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(userId, userDetails.getUser());
    }
}