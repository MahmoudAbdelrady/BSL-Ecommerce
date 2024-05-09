package com.bslecommerce.springbackend.service;

import com.bslecommerce.springbackend.dto.Cart.CartDTO;
import com.bslecommerce.springbackend.dto.Cart.CartItemDTO;
import com.bslecommerce.springbackend.dto.Cart.CartItemOpDTO;
import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.model.Cart.Cart;
import com.bslecommerce.springbackend.model.Cart.CartItem;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.repository.Cart.CartItemRepository;
import com.bslecommerce.springbackend.repository.Cart.CartRepository;
import com.bslecommerce.springbackend.repository.Item.ItemRepository;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CartService(CartRepository cartRepository, ItemRepository itemRepository, CartItemRepository cartItemRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<Object> getUserCart() throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartRepository.fetchUserCart(UUID.fromString(userInfo.getUserId()));
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            CartDTO cartDTO = new CartDTO();
            cartDTO.setCartId(cart.getCartId());
            List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream().map(cartItem -> {
                Item item = cartItem.getItem();
                CartItemDTO cartItemDTO = modelMapper.map(item, CartItemDTO.class);
                if (item.getCategory() != null) {
                    cartItemDTO.setCategory(item.getCategory().getTitle());
                }
                if (item.getQuantity() < cartItem.getQuantity()) {
                    cartItem.setQuantity(item.getQuantity());
                    cartItem.setPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    cartItem = cartItemRepository.save(cartItem);
                }
                cartItem.setItem(null);
                modelMapper.map(cartItem, cartItemDTO);
                return cartItemDTO;
            }).toList();
            cartDTO.setCartItems(cartItemDTOS);
            return ResponseEntity.ok(ResponseMaker.successRes("Cart retrieved successfully", cartDTO));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> CartItemOpCart(CartItemOpDTO cartItemOpDTO) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Cart cart = cartRepository.findUserCart(UUID.fromString(userInfo.getUserId()));
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            Item item = itemRepository.findItemByItemId(cartItemOpDTO.getItemId());
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
            }
            if (item.getQuantity() == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Item isn't available for purchasing right now."));
            }
            if (cartItemOpDTO.getAction().equals("add")) {
                CartItem existingCartItem = cartItemRepository.findCartItemByItemIdAndCartId(item.getItemId(), cart.getCartId());
                if (existingCartItem == null) {
                    if (item.getQuantity() < cartItemOpDTO.getQuantity()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Available quantity of the item is less than the requested."));
                    }
                    CartItem cartItem = new CartItem();
                    cartItem.setItem(item);
                    cartItem.setCart(cart);
                    cartItem.setQuantity(cartItemOpDTO.getQuantity());
                    BigDecimal reqQuantity = BigDecimal.valueOf(cartItemOpDTO.getQuantity());
                    cartItem.setPrice(reqQuantity.multiply(item.getPrice()));
                    cart.AddToCart(cartItem);
                } else {
                    if (item.getQuantity() < existingCartItem.getQuantity() + cartItemOpDTO.getQuantity()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Available quantity of the item is less than the requested."));
                    }
                    existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemOpDTO.getQuantity());
                    BigDecimal reqQuantity = BigDecimal.valueOf(cartItemOpDTO.getQuantity());
                    existingCartItem.setPrice(reqQuantity.multiply(item.getPrice()).add(existingCartItem.getPrice()));
                }
                cartRepository.save(cart);
                return ResponseEntity.ok(ResponseMaker.successRes("Item added successfully to the cart", null));
            } else if (cartItemOpDTO.getAction().equals("rall")) {
                if (cartItemOpDTO.getCartItemId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Cart Item id is required"));
                }
                CartItem cartItem = cartItemRepository.findCartItemById(cartItemOpDTO.getCartItemId());
                boolean res = cart.RemoveTotalFromCart(cartItem);
                if (res) {
                    cartItemRepository.delete(cartItem);
                    return ResponseEntity.ok(ResponseMaker.successRes("Item removed from the cart successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item is not in the cart"));
                }
            } else {
                if (cartItemOpDTO.getCartItemId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Cart Item id is required"));
                }
                CartItem cartItem = cartItemRepository.findCartItemById(cartItemOpDTO.getCartItemId());
                String res = cart.RemoveFromCart(cartItem);
                if (res.equals("d")) {
                    cartItem.setPrice(cartItem.getPrice().subtract(item.getPrice()));
                    cartItemRepository.save(cartItem);
                    return ResponseEntity.ok(ResponseMaker.successRes("Item quantity decreased in the cart successfully", null));
                } else if (res.equals("r")) {
                    cartItemRepository.delete(cartItem);
                    return ResponseEntity.ok(ResponseMaker.successRes("Item removed from the cart successfully", null));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item is not in the cart"));
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
