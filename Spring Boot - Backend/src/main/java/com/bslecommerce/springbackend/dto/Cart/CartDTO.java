package com.bslecommerce.springbackend.dto.Cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CartDTO {
    private UUID cartId;
    private List<CartItemDTO> cartItems;
}
