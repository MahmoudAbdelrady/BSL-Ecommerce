package com.bslecommerce.springbackend.dto.Cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CartItemOpDTO {
    private UUID itemId;

    private UUID cartItemId;

    private Integer quantity = 1;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "add|rem|rall", message = "Action must be one of the following: [add - rem - rall]")
    private String action;
}
