package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.entity.Restaurant;
import com.sparta.outsourcing.enums.StatusEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, RestaurantRepositoryQuery {
    Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Restaurant> findByUserId(Long id);
    Optional<Restaurant> findByIdAndStatus(Long id, StatusEnum status);
}
