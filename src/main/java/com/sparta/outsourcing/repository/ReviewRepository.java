package com.sparta.outsourcing.repository;

import com.sparta.outsourcing.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryQuery {
    Page<Review> findAll(Pageable pageable);
}