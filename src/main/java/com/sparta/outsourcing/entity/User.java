package com.sparta.outsourcing.entity;

import com.sparta.outsourcing.dto.ProfileDto;
import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.enums.StatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String userinfo;

    @Setter
    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ACTIVE;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Restaurant> restaurants = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> deniedPassword = new ArrayList<>(3);

    @Column(nullable = true)
    private String refreshtoken;

    @Setter
    @Column
    private boolean expired = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantLike> restaurantLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_follows", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_user_id"))
    private Set<User> follows = new HashSet<>();

    @ManyToMany(mappedBy = "follows", fetch = FetchType.EAGER)
    private Set<User> followers = new HashSet<>();

    public User(String username, String password, String nickname, String userinfo) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userinfo = userinfo;
    }

    // 프로필 저장
    public void updateProfile(ProfileDto profileDto) {
        this.password = profileDto.getPassword();
        this.nickname = profileDto.getNickname();
        this.userinfo = profileDto.getUserinfo();
        setDeniedPassword(profileDto.getPassword());
    }

    // 최근 변경한 비밀번호 저장
    private void setDeniedPassword(String password){
        if(deniedPassword.size() > 2){
            deniedPassword.remove(0);
        }
        deniedPassword.add(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //토큰 값 초기화
    public void updateRefreshToken(String refreshToken) {
        this.refreshtoken = refreshToken;
    }
    // 삭제처리
    public void deleteUser() {
        this.status = StatusEnum.DENIED;
        this.setDeletedAt(LocalDateTime.now());
    }
    // 내가 좋아요한 게시글 수
    public int getLikedRestaurantCount() {
        return this.restaurantLikes.size();
    }

    // 내가 좋아요한 댓글 수
    public int getLikedReviewCount() {
        return this.reviewLikes.size();
    }

    // 팔로우 기능
    public void follow(User user) {
        if (user == null || user.equals(this)) {
            return;
        }
        this.follows.add(user);
        user.followers.add(this);
    }

    // 언팔로우 기능
    public void unfollow(User user) {
        if (user == null || user.equals(this)) {
            return;
        }
        this.follows.remove(user);
        user.followers.remove(this);
    }

    // 팔로우 여부 확인
    public boolean isFollowing(User user) {
        System.out.println(this.follows);
        System.out.println(user);
        return this.follows.contains(user);
    }

    // 내가 팔로우하는 사용자 목록 조회
    public Set<User> getFollowingUsers() {
        return this.follows;
    }

    // 나를 팔로우하는 사용자 목록 조회
    public Set<User> getFollowerUsers() {
        return this.followers;
    }
}