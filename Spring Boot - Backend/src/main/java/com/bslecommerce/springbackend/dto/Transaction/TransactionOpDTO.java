package com.bslecommerce.springbackend.dto.Transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransactionOpDTO {
    @NotNull(message = "Transaction id is required")
    private UUID transactionId;

    @NotBlank(message = "Transaction status is required")
    @Pattern(regexp = "^(PENDING|CONFIRMED|ARRIVING|DELIVERED)$",
            message = "Transaction status must be one of the following [PENDING - CONFIRMED - ARRIVING - DELIVERED]")
    private String status;
}
