package com.bslecommerce.springbackend.repository.Item;

import com.bslecommerce.springbackend.model.Category;
import com.bslecommerce.springbackend.model.Item.Item;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT it FROM Item it LEFT JOIN FETCH it.category")
    Page<Item> fetchAllItems(Pageable pageable);

    @Query("SELECT it FROM Item it JOIN FETCH it.category c WHERE c.catId=:catId")
    Page<Item> findItemByCatId(@Param("catId") UUID catId, Pageable pageable);

    @Query("SELECT it FROM Item it LEFT JOIN FETCH it.category WHERE it.itemId=:itemId")
    Item fetchItemById(@Param("itemId") UUID itemId);

    Item findItemByItemId(UUID itemId);

    Item findItemByTitleIgnoreCase(String title);

    @Modifying
    @Transactional
    @Query("UPDATE Item i SET i.category = null WHERE i.category =:category")
    void updateItemsCategoryToNullByCategory(@Param("category") Category category);
}
