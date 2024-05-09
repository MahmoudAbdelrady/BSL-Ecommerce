package com.bslecommerce.springbackend.model.Item;

import com.bslecommerce.springbackend.model.Cart.CartItem;
import com.bslecommerce.springbackend.model.Category;
import com.bslecommerce.springbackend.model.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "items")
@Entity
@Getter
@Setter
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private UUID itemId;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "VARCHAR(700)")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal stars;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @CreationTimestamp
    private Date addedAt;

    public Item() {
        this.itemId = UUID.randomUUID();
        this.stars = BigDecimal.valueOf(0.0);
        this.reviews = new ArrayList<>();
    }

    public void AddReviewToItem(Review review) {
        reviews.add(review);
    }

    public boolean RemoveReviewFromItem(Review review) {
        if (reviews.contains(review)) {
            reviews.remove(review);
            return true;
        }
        return false;
    }

    public void CalculateAverageStars() {
        if (!this.reviews.isEmpty()) {
            double totalStars = 0.0;
            for (Review review : this.reviews) {
                totalStars += review.getStars();
            }
            double avgStars = totalStars / this.reviews.size();
            this.stars = BigDecimal.valueOf(avgStars);
        } else {
            this.stars = BigDecimal.valueOf(0.0);
        }
    }
}
