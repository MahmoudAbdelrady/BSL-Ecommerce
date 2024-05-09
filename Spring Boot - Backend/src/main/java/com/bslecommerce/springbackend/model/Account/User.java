package com.bslecommerce.springbackend.model.Account;

import com.bslecommerce.springbackend.model.Cart.Cart;
import com.bslecommerce.springbackend.model.Item.UserFavItem;
import com.bslecommerce.springbackend.model.Item.UserPurItem;
import com.bslecommerce.springbackend.model.Review;
import com.bslecommerce.springbackend.model.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Table(name = "users")
@Entity
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstname;

    private String lastname;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(unique = true)
    private String phoneNumber;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserPurItem> purchasedItems;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserFavItem> favItems;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @CreationTimestamp
    private Date createdAt;

    public User() {
        this.userId = UUID.randomUUID();
        this.photo = "https://static.vecteezy.com/system/resources/thumbnails/024/983/914/small_2x/simple-user-default-icon-free-png.png";
        this.transactions = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.purchasedItems = new ArrayList<>();
        this.favItems = new ArrayList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public <T> boolean AddItem(List<T> itemList, T item) {
        if (itemList.contains(item)) {
            return false;
        }
        itemList.add(item);
        return true;
    }

    public <T> void RemoveItem(List<T> itemList, T item) {
        itemList.remove(item);
    }
}
