package com.bslecommerce.springbackend.dto.User;

import com.bslecommerce.springbackend.dto.ReviewDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID userId;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,}$", message = "Username must be at least 5 characters long and contain only letters and numbers")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    private String firstname;

    private String lastname;

    private String photo;

    private String phoneNumber;

    private List<ReviewDTO> reviews;

    private List<TransactionDTO> transactions;
}
