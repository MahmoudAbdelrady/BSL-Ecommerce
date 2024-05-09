package com.bslecommerce.springbackend.dto.Item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ItemOpDTO {
    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(add|rem)$", message = "Action must be 'add' or 'rem'")
    private String action;
}
