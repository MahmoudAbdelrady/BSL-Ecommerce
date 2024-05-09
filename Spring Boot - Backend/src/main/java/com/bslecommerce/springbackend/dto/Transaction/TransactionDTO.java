package com.bslecommerce.springbackend.dto.Transaction;

import com.bslecommerce.springbackend.dto.Cart.CartItemDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TransactionDTO {
    private UUID transactionId;

    private BigDecimal totalPrice;

    private String status;

    private List<CartItemDTO> cartItems;

    private Date createdAt;
}
