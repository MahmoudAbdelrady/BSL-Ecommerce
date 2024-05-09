package com.bslecommerce.springbackend.repository.Item;

import com.bslecommerce.springbackend.model.Item.UserFavItem;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserFavItemRepository extends JpaRepository<UserFavItem, Integer> {
    @Query("SELECT fI FROM UserFavItem fI JOIN FETCH fI.item WHERE fI.user.userId=:userId")
    Page<UserFavItem> fetchFavItemsByUserId(@Param("userId") UUID userId, Pageable pageable);
    @Query("SELECT ufi FROM UserFavItem ufi WHERE ufi.user.userId=:userId AND ufi.item.itemId=:itemId")
    UserFavItem findByUserIdAndItemId(UUID userId, UUID itemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFavItem ufi WHERE ufi.item.itemId=:itemId")
    void deleteFavItemsByItemId(@Param("itemId") UUID itemId);
}
