package com.sparta.outsourcing.controller;

import com.sparta.outsourcing.dto.ReviewDto;
import com.sparta.outsourcing.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody ReviewDto reviewDto) {
        return reviewService.addReview(reviewDto);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews(@RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "5") int size) {
        return reviewService.getAllReviews(page, size);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody ReviewDto reviewDto) {
        return reviewService.updateReview(reviewId, reviewDto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("reviewId") Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }
}