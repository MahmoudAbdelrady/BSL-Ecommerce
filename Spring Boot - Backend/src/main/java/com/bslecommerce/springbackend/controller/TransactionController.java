package com.bslecommerce.springbackend.controller;

import com.bslecommerce.springbackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Object> GetUserTransactions(@RequestParam("p") Integer page) throws Exception {
        return transactionService.getUserTransactions(page);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Object> GetUserTransactionById(@PathVariable UUID transactionId) throws Exception {
        return transactionService.getUserTransactionById(transactionId);
    }

    @PostMapping
    public ResponseEntity<Object> CreateTransaction() throws Exception {
        return transactionService.createTransaction();
    }

    @PostMapping("/{transactionId}/cancel")
    public ResponseEntity<Object> CancelTransaction(@PathVariable UUID transactionId) throws Exception {
        return transactionService.cancelTransaction(transactionId);
    }
}
