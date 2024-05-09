package com.bslecommerce.springbackend.dto.Cart;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CartItemDTO {
    private UUID itemId;

    private UUID cartItemId;

    private String title;

    private String photo;

    private String category;

    private Integer quantity;

    private BigDecimal price;
}
