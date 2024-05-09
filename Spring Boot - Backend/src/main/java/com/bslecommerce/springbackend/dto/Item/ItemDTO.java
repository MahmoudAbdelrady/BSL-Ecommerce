package com.bslecommerce.springbackend.dto.Item;

import com.bslecommerce.springbackend.dto.ReviewDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ItemDTO {
    private UUID itemId;

    private String category;

    @NotBlank(message = "Item title is required")
    private String title;

    @NotBlank(message = "Item description is required")
    private String description;

    private String photo;

    @NotNull(message = "Item quantity is required")
    @Min(value = 0, message = "Item quantity can't be less than 0")
    @Max(value = 1000000, message = "Item quantity can't be more than 1000000")
    private Integer quantity;

    @NotNull(message = "Item price is required")
    @Min(value = 0, message = "Item price can't be less than 0")
    @Max(value = 1000000, message = "Item price can't be more than 1000000")
    private BigDecimal price;

    private Integer stars;

    private List<ReviewDTO> reviews;
}
