package com.bslecommerce.springbackend.repository.Cart;

import com.bslecommerce.springbackend.model.Cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    @Query("SELECT cI FROM CartItem cI WHERE cI.cartItemId=:cartItemId")
    CartItem findCartItemById(@Param("cartItemId") UUID cartItemId);

    @Query("SELECT cI FROM CartItem cI WHERE cI.item.itemId=:itemId AND cI.cart.cartId=:cartId")
    CartItem findCartItemByItemIdAndCartId(@Param("itemId") UUID itemId, @Param("cartId") UUID cartId);
}
