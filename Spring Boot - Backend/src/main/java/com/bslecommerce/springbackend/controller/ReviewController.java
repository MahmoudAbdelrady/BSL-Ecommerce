package com.bslecommerce.springbackend.controller;

import com.bslecommerce.springbackend.dto.ReviewDTO;
import com.bslecommerce.springbackend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> GetItemReviews(@PathVariable UUID itemId, @RequestParam("p") Integer page) throws Exception {
        return reviewService.getItemReviews(itemId, page);
    }

    @GetMapping("/user")
    public ResponseEntity<Object> GetUserReviews(@RequestParam("p") Integer page) throws Exception {
        return reviewService.getUserReviews(page);
    }

    @PostMapping
    public ResponseEntity<Object> CreateReview(@RequestBody @Valid ReviewDTO reviewDTO) throws Exception {
        return reviewService.createReview(reviewDTO);
    }

    @PutMapping
    public ResponseEntity<Object> EditReview(@RequestBody @Valid ReviewDTO reviewDTO) throws Exception {
        return reviewService.editReview(reviewDTO);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Object> DeleteReviewById(@PathVariable UUID reviewId) throws Exception {
        return reviewService.deleteReviewById(reviewId);
    }
}
