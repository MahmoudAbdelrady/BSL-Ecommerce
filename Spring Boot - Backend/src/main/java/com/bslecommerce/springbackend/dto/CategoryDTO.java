package com.bslecommerce.springbackend.dto;

import com.bslecommerce.springbackend.dto.Item.ItemDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CategoryDTO {
    private UUID catId;
    @NotBlank(message = "Category Title is required")
    private String title;

    private String photo;

    private List<ItemDTO> items;
}
