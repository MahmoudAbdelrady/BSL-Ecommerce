package com.bslecommerce.springbackend.repository.Item;

import com.bslecommerce.springbackend.model.Item.UserPurItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserPurItemRepository extends JpaRepository<UserPurItem, Integer> {
    @Query("SELECT upi FROM UserPurItem upi WHERE upi.user.username=:username AND upi.item.itemId=:itemId")
    UserPurItem findByUsernameAndItemId(@Param("username") String username, @Param("itemId") UUID itemId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserPurItem upi WHERE upi.item.itemId=:itemId")
    void deletePurItemByItemId(@Param("itemId") UUID itemId);
}
