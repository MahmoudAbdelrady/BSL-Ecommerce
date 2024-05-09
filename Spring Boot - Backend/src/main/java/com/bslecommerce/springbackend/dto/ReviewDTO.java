package com.bslecommerce.springbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {
    private UUID reviewId;

    private UUID itemId;

    private String itemName;

    private String itemPhoto;

    private String username;

    private String photo;

    @NotBlank(message = "Review title is required")
    private String title;

    @NotBlank(message = "Review description is required")
    private String description;

    @NotNull(message = "Review Stars are required")
    private Integer stars;

    private Date createdAt;

    private Date modifiedAt;
}
