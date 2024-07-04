package com.sparta.outsourcing.dto;

import com.sparta.outsourcing.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReviewDto {

    private Long orderId;
    private String content;
    private Long likes;

    public ReviewDto(Long orderId, String content, Long likes) {
        this.orderId = orderId;
        this.content = content;
        this.likes = likes;
    }

    public static Page<ReviewDto> mapFromPage(Page<Review> page) {
        List<ReviewDto> dtos = page.getContent().stream()
                .map(review -> new ReviewDto(
                        review.getOrder().getOrderId(),
                        review.getContent(),
                        review.getLikes()
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }
}
