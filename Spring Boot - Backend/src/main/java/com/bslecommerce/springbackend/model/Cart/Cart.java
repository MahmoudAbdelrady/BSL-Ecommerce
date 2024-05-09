package com.bslecommerce.springbackend.model.Cart;

import com.bslecommerce.springbackend.model.Account.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "carts")
@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private UUID cartId;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    public Cart() {
        this.cartId = UUID.randomUUID();
        this.cartItems = new ArrayList<>();
    }

    public void AddToCart(CartItem cartItem) {
        cartItems.add(cartItem);
    }

    public boolean RemoveTotalFromCart(CartItem cartItem) {
        if (cartItems.contains(cartItem)) {
            cartItems.remove(cartItem);
            return true;
        }
        return false;
    }

    public String RemoveFromCart(CartItem cartItem) {
        if (cartItems.contains(cartItem)) {
            if (cartItem.getQuantity() == 1) {
                cartItems.remove(cartItem);
                return "r";
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                return "d";
            }
        } else {
            return "n";
        }
    }
}
