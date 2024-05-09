package com.bslecommerce.springbackend.repository.Cart;

import com.bslecommerce.springbackend.model.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.userId=:userId")
    Cart findUserCart(@Param("userId") UUID userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.user.userId=:userId")
    Cart fetchUserCart(@Param("userId") UUID userId);
}
