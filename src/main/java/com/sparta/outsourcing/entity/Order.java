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
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ACTIVE;

    private String orderStatus;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ElementCollection // 컬렉션 객체임을 알려줌
    @CollectionTable(name = "menu_order", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "menu_count")
    private List<String> menuList = new ArrayList<>();

    public Order(User user, int totalPrice) {
        this.user = user;
        this.totalPrice = totalPrice;
    }

    public void delete() {
        setDeletedAt(LocalDateTime.now());
        this.status = StatusEnum.DENIED;
    }
}
