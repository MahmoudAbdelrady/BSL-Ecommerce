package com.bslecommerce.springbackend.model;

import com.bslecommerce.springbackend.model.Item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "categories")
@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private UUID catId;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Item> items;

    @CreationTimestamp
    private Date addedAt;

    public Category() {
        this.catId = UUID.randomUUID();
        this.items = new ArrayList<>();
    }

    public boolean AddItemToCategory(Item item) {
        if (items.contains(item)) {
            return false;
        }
        items.add(item);
        return true;
    }

    public boolean RemoveItemFromCategory(Item item) {
        if (items.contains(item)) {
            items.remove(item);
            return true;
        }
        return false;
    }
}
