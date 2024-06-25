package com.sparta.outsourcing.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.outsourcing.dto.MenuDto;
import com.sparta.outsourcing.enums.StatusEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@NoArgsConstructor
public class Menu extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private Long price;

    @Setter
    @Enumerated(EnumType.STRING)
    private StatusEnum status = StatusEnum.ACTIVE;

    public Menu(Restaurant restaurant, String menuName, Long price) {
        this.restaurant = restaurant;
        this.menuName = menuName;
        this.price = price;
    }
    public void update(MenuDto menuDto) {
        this.menuName = menuDto.getMenuName();
    }
    public void delete() {
        setDeletedAt(LocalDateTime.now());
        setStatus(StatusEnum.DENIED);
    }
}

