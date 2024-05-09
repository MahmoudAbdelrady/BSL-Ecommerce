package com.bslecommerce.springbackend.model;

import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.model.Cart.CartItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "transactions")
@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private UUID transactionId;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @CreationTimestamp
    private Date createdAt;

    public Transaction() {
        this.transactionId = UUID.randomUUID();
        this.status = 0;
    }
}
