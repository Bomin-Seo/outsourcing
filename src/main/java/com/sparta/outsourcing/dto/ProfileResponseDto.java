package com.sparta.outsourcing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ProfileResponseDto {
    private String nickname;
    private String userinfo;
    // 게시글 수 설정
    @Setter
    private int likedRestaurantCount;
    // 댓글 수 설정
    @Setter
    private int likedReviewCount;

    public ProfileResponseDto(String nickname, String userinfo) {
        this.nickname = nickname;
        this.userinfo = userinfo;
    }

}
