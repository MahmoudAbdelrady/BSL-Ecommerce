package com.bslecommerce.springbackend.repository;

import com.bslecommerce.springbackend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query("SELECT r FROM Review r JOIN FETCH r.item it JOIN FETCH r.user WHERE it.itemId = :itemId")
    Page<Review> findReviewsByItemId(@Param("itemId") UUID itemId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.reviewId=:reviewId")
    Review findReviewByReviewId(@Param("reviewId") UUID reviewId);

    @Query("SELECT r FROM Review r WHERE r.user.username=:username AND r.item.itemId=:itemId")
    Review findReviewByUsernameAndItemId(@Param("username") String username, @Param("itemId") UUID itemId);

    @Query("SELECT r FROM Review r WHERE r.user.userId=:userId")
    Page<Review> findReviewsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
