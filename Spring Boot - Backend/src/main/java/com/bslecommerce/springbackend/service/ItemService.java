package com.bslecommerce.springbackend.service;

import com.bslecommerce.springbackend.dto.Item.ItemDTO;
import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.dto.ReviewDTO;
import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.model.Item.UserFavItem;
import com.bslecommerce.springbackend.model.Review;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.repository.Item.ItemRepository;
import com.bslecommerce.springbackend.repository.Item.UserFavItemRepository;
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
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserFavItemRepository favItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserRepository userRepository,
                       ReviewRepository reviewRepository, UserFavItemRepository favItemRepository, ModelMapper modelMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.favItemRepository = favItemRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<Object> getAllItems(Integer idx) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<Item> items = itemRepository.fetchAllItems(pageRequest).getContent();
            List<ItemDTO> itemDTOS = items.stream().map(item -> {
                item.setReviews(null);
                ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);
                if (item.getCategory() != null) {
                    itemDTO.setCategory(item.getCategory().getTitle());
                }
                return itemDTO;
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Retrieved items successfully", itemDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> getItemById(UUID itemId) throws Exception {
        try {
            Item item = itemRepository.fetchItemById(itemId);
            if (item != null) {
                PageRequest pageRequest = PageRequest.of(0, 10);
                List<Review> reviews = reviewRepository.findReviewsByItemId(item.getItemId(), pageRequest).getContent();
                List<ReviewDTO> reviewDTOS = reviews.stream().map(review -> {
                    ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                    reviewDTO.setItemId(review.getReviewId());
                    reviewDTO.setUsername(review.getUser().getUsername());
                    reviewDTO.setPhoto(review.getUser().getPhoto());
                    return reviewDTO;
                }).toList();
                item.setReviews(null);
                ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);
                if (item.getCategory() != null) {
                    itemDTO.setCategory(item.getCategory().getTitle());
                }
                itemDTO.setReviews(reviewDTOS);
                return ResponseEntity.ok(ResponseMaker.successRes("Retrieved item successfully", itemDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> getFavItems(Integer idx) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<UserFavItem> favItems = favItemRepository.fetchFavItemsByUserId(user.getUserId(), pageRequest).getContent();
            List<ItemDTO> itemDTOS = favItems.stream().map(favItem -> {
                Item item = favItem.getItem();
                item.setReviews(null);
                item.setCartItems(null);
                return modelMapper.map(item, ItemDTO.class);
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Retrieved favorite items successfully", itemDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> itemOpFavItems(ItemOpDTO itemOpDTO) throws Exception {
        try {
            Item item = itemRepository.findItemByItemId(itemOpDTO.getItemId());
            if (item != null) {
                UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
                }
                UserFavItem existingFavItem = favItemRepository.findByUserIdAndItemId(UUID.fromString(userInfo.getUserId()), item.getItemId());
                if (itemOpDTO.getAction().equals("add")) {
                    if (existingFavItem != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Item already exists in your favorites"));
                    }
                    UserFavItem userFavItem = new UserFavItem(user, item);
                    user.AddItem(user.getFavItems(), userFavItem);
                    favItemRepository.save(userFavItem);
                    return ResponseEntity.ok(ResponseMaker.successRes("Item added to favorites successfully", null));
                } else {
                    if (existingFavItem == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item is not in your favorites"));
                    }
                    user.RemoveItem(user.getFavItems(), existingFavItem);
                    favItemRepository.delete(existingFavItem);
                    return ResponseEntity.ok(ResponseMaker.successRes("Item removed from favorites successfully", null));
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
