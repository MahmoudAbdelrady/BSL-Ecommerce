package com.bslecommerce.springbackend.service;

import com.bslecommerce.springbackend.dto.Cart.CartItemDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionDTO;
import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.model.Cart.Cart;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.model.Transaction;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.repository.*;
import com.bslecommerce.springbackend.repository.Account.UserRepository;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import com.bslecommerce.springbackend.util.TransactionStatusMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TransactionStatusMapper transMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                              TransactionStatusMapper transMapper, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.transMapper = transMapper;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<Object> getUserTransactions(Integer idx) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<Transaction> transactions = transactionRepository.findAllByUserId(user.getUserId(), pageRequest).getContent();
            List<TransactionDTO> transactionDTOS = transactions.stream().map(transaction -> {
                transaction.setCartItems(null);
                TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
                transactionDTO.setStatus(transMapper.getIntToStatus().get(transaction.getStatus()));
                return transactionDTO;
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("User transactions retrieved successfully", transactionDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> getUserTransactionById(UUID transactionId) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Transaction transaction = transactionRepository.fetchUserTransactionById(UUID.fromString(userInfo.getUserId()), transactionId);
            if (transaction == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Transaction not found"));
            }
            TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
            List<CartItemDTO> cartItemDTOS = transaction.getCartItems().stream().map(cartItem -> {
                CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);
                Item item = cartItem.getItem();
                cartItemDTO.setItemId(item.getItemId());
                cartItemDTO.setTitle(item.getTitle());
                cartItemDTO.setPhoto(item.getPhoto());
                if (item.getCategory() != null) {
                    cartItemDTO.setCategory(item.getCategory().getTitle());
                }
                return cartItemDTO;
            }).toList();
            transactionDTO.setCartItems(cartItemDTOS);
            transactionDTO.setStatus(transMapper.getIntToStatus().get(transaction.getStatus()));
            return ResponseEntity.ok(ResponseMaker.successRes("User Transaction retrieved successfully", transactionDTO));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> createTransaction() throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.fetchUserWithCartByUsername(userInfo.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            Cart cart = user.getCart();
            if (cart.getCartItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Cart is empty. Can't perform any transactions"));
            }

            AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.valueOf(0.0));
            AtomicReference<Transaction> transaction = new AtomicReference<>(new Transaction());

            List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                    .filter(cartItem -> {
                        Item item = cartItem.getItem();
                        if (item.getQuantity() < cartItem.getQuantity()) {
                            throw new IllegalArgumentException("Quantity exceeds available stock for item: " + item.getTitle());
                        }
                        return true;
                    })
                    .map(cartItem -> {
                        Item item = cartItem.getItem();
                        item.setQuantity(item.getQuantity() - cartItem.getQuantity());
                        totalPrice.updateAndGet(value -> value.add(cartItem.getPrice()));
                        cartItem.setCart(null);
                        cartItem.setTransaction(transaction.get());
                        CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);
                        cartItemDTO.setItemId(item.getItemId());
                        cartItemDTO.setTitle(item.getTitle());
                        cartItemDTO.setPhoto(item.getPhoto());
                        if (item.getCategory() != null) {
                            cartItemDTO.setCategory(item.getCategory().getTitle());
                        }
                        return cartItemDTO;
                    })
                    .toList();

            transaction.get().setCartItems(cart.getCartItems());
            transaction.get().setUser(user);
            transaction.get().setTotalPrice(totalPrice.get());
            boolean isAdded = user.AddItem(user.getTransactions(), transaction.get());
            if (!isAdded) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Transaction already made"));
            }

            cart.setCartItems(null);
            Transaction savedTransaction = transactionRepository.save(transaction.get());
            TransactionDTO transactionDTO = modelMapper.map(savedTransaction, TransactionDTO.class);
            transactionDTO.setCartItems(cartItemDTOS);
            transactionDTO.setStatus(transMapper.getIntToStatus().get(savedTransaction.getStatus()));
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMaker.successRes("Transaction created successfully.", transactionDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes(e.getMessage()));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> cancelTransaction(UUID transactionId) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.fetchUserWithCartByUsername(userInfo.getUsername());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            Transaction transaction = transactionRepository.findByTransactionId(transactionId);
            if (transaction != null) {
                if (transaction.getStatus() >= 1) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("Transaction already confirmed, Can't cancel."));
                }
                if (transaction.getUser() != user) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMaker.errorRes("You are not authorized to cancel this transaction."));
                }
                transaction.getCartItems().forEach(cartItem -> {
                    Item item = cartItem.getItem();
                    item.setQuantity(item.getQuantity() + cartItem.getQuantity());
                });
                transactionRepository.delete(transaction);
                return ResponseEntity.ok(ResponseMaker.successRes("Transaction cancelled successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Transaction not found"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
