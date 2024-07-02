package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.dto.MenuDto;
import com.sparta.outsourcing.entity.Menu;
import com.sparta.outsourcing.enums.StatusEnum;
import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<MenuDto> findByRestaurantId(Long restaurantId);
    Optional<Menu> findByRestaurantIdAndMenuId(Long restaurantId, Long menuId);
}