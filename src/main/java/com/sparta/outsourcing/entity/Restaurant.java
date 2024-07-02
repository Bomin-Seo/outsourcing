package com.sparta.outsourcing.entity;

import com.sparta.outsourcing.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private String restaurantInfo;

    @Column(nullable = false)
    private String phoneNumber;

    @Setter
    @Column(nullable = false)
    private StatusEnum status = StatusEnum.ACTIVE;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false)
    private Long likes = 0L;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantLike> restaurantLikes = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Restaurant(User user, String restaurantName, String restaurantInfo, String phoneNumber) {
        this.user = user;
        this.restaurantName = restaurantName;
        this.restaurantInfo = restaurantInfo;
        this.phoneNumber = phoneNumber;
        this.likes = 0L;
    }
    public void update(String restaurantName, String restaurantInfo, String phoneNumber) {
        this.restaurantName = restaurantName;
        this.restaurantInfo = restaurantInfo;
        this.phoneNumber = phoneNumber;
    }


    public Long updateLike(boolean islike){
        if(islike){this.likes += 1;}
        else{this.likes -= 1;}
        return this.likes;
    }
    public void delete() {
        setDeletedAt(LocalDateTime.now());
        this.status = StatusEnum.DENIED;
    }
}
