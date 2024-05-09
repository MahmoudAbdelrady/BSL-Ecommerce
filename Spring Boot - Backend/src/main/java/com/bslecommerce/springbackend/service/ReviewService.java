package com.bslecommerce.springbackend.service;

import com.bslecommerce.springbackend.dto.ReviewDTO;
import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.model.Item.UserPurItem;
import com.bslecommerce.springbackend.model.Review;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.repository.Item.ItemRepository;
import com.bslecommerce.springbackend.repository.Item.UserPurItemRepository;
import com.bslecommerce.springbackend.repository.ReviewRepository;
import com.bslecommerce.springbackend.repository.Account.UserRepository;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserPurItemRepository purItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ItemRepository itemRepository,
                         UserRepository userRepository, UserPurItemRepository purItemRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.purItemRepository = purItemRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<Object> getItemReviews(UUID itemId, Integer idx) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<Review> reviews = reviewRepository.findReviewsByItemId(itemId, pageRequest).getContent();
            List<ReviewDTO> reviewDTOS = reviews.stream().map(review -> {
                review.setItem(null);
                ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                reviewDTO.setItemId(itemId);
                reviewDTO.setUsername(review.getUser().getUsername());
                reviewDTO.setPhoto(review.getUser().getPhoto());
                return reviewDTO;
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Item reviews retrieved successfully", reviewDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> getUserReviews(Integer idx) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(idx, 10);
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            List<Review> reviews = reviewRepository.findReviewsByUserId(UUID.fromString(userInfo.getUserId()), pageRequest).getContent();
            List<ReviewDTO> reviewDTOS = reviews.stream().map(review -> {
                ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                reviewDTO.setItemId(review.getItem().getItemId());
                reviewDTO.setItemName(review.getItem().getTitle());
                reviewDTO.setItemPhoto(review.getItem().getPhoto());
                reviewDTO.setUsername(user.getUsername());
                reviewDTO.setPhoto(user.getPhoto());
                return reviewDTO;
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Item reviews retrieved successfully", reviewDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> createReview(ReviewDTO reviewDTO) throws Exception {
        try {
            Item item = itemRepository.findItemByItemId(reviewDTO.getItemId());
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
            }

            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            UserPurItem purItem = purItemRepository.findByUsernameAndItemId(userInfo.getUsername(), reviewDTO.getItemId());
            if (purItem == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You should purchase this item to be able to review it"));
            }
            Review existingReview = reviewRepository.findReviewByUsernameAndItemId(userInfo.getUsername(), reviewDTO.getItemId());

            if (existingReview != null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You already reviewed this item"));
            }

            Review review = modelMapper.map(reviewDTO, Review.class);

            review.setItem(item);
            review.setUser(user);
            user.AddItem(user.getReviews(), review);
            item.AddReviewToItem(review);
            item.CalculateAverageStars();
            review = reviewRepository.save(review);

            modelMapper.map(review, reviewDTO);
            reviewDTO.setUsername(user.getUsername());
            reviewDTO.setPhoto(user.getPhoto());
            reviewDTO.setItemName(item.getTitle());
            reviewDTO.setItemPhoto(item.getPhoto());

            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMaker.successRes("Review created successfully", reviewDTO));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> editReview(ReviewDTO reviewDTO) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            Review review = reviewRepository.findReviewByReviewId(reviewDTO.getReviewId());
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Review not found"));
            }
            if (!review.getUser().getUserId().equals(UUID.fromString(userInfo.getUserId()))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You aren't allowed to edit this review"));
            }
            if (review.getCreatedAt().getTime() - System.currentTimeMillis() > 1209600000) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You can't edit this review after 2 weeks of creating it"));
            }
            review.setTitle(reviewDTO.getTitle());
            review.setDescription(reviewDTO.getDescription());
            review.setStars(reviewDTO.getStars());
            review.getItem().CalculateAverageStars();
            review = reviewRepository.save(review);
            modelMapper.map(review, reviewDTO);
            reviewDTO.setUsername(user.getUsername());
            reviewDTO.setPhoto(user.getPhoto());
            reviewDTO.setItemId(review.getItem().getItemId());
            reviewDTO.setItemName(review.getItem().getTitle());
            reviewDTO.setItemPhoto(review.getItem().getPhoto());
            return ResponseEntity.ok(ResponseMaker.successRes("Review edited successfully", reviewDTO));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> deleteReviewById(UUID reviewId) throws Exception {
        try {
            Review review = reviewRepository.findReviewByReviewId(reviewId);
            if (review != null) {
                UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!review.getUser().getUserId().equals(UUID.fromString(userInfo.getUserId()))) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You aren't allowed to delete this review"));
                }
                Item item = review.getItem();
                boolean isRemoved = item.RemoveReviewFromItem(review);
                if (!isRemoved) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Review not found"));
                }
                item.CalculateAverageStars();
                reviewRepository.delete(review);
                return ResponseEntity.ok(ResponseMaker.successRes("Review deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Review not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
