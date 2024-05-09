package com.bslecommerce.springbackend.repository;

import com.bslecommerce.springbackend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.user.userId=:userId")
    Page<Transaction> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.cartItems WHERE t.user.userId=:userId AND t.transactionId=:transactionId")
    Transaction fetchUserTransactionById(UUID userId, UUID transactionId);

    Transaction findByTransactionId(UUID transactionId);
}
