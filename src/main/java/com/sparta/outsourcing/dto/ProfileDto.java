package com.sparta.outsourcing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ProfileDto {
    @NotBlank(message = "필수로 입력해야됩니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()]).{8,15}$", message = "비밀번호는 특수문자포함 최소 8자, 최대 15자입니다.")
    private String password;

    @NotBlank(message = "필수로 입력해야됩니다.")
    private String nickname;
    private String userinfo;

    public ProfileDto(String nickname, String userinfo, String password) {
        this.nickname = nickname;
        this.userinfo = userinfo;
        this.password = password;
    }
}