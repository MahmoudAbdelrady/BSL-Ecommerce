package com.bslecommerce.springbackend.controller;

import com.bslecommerce.springbackend.dto.Cart.CartItemOpDTO;
import com.bslecommerce.springbackend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Object> GetUserCart() throws Exception {
        return cartService.getUserCart();
    }

    @PostMapping
    public ResponseEntity<Object> CartItemOpCart(@RequestBody @Valid CartItemOpDTO cartItemOpDTO) throws Exception {
        return cartService.CartItemOpCart(cartItemOpDTO);
    }
}
