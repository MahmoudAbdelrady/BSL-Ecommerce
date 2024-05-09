package com.bslecommerce.springbackend.model.Item;

import com.bslecommerce.springbackend.model.Account.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "users_fav_items")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserFavItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "item_id")
    private Item item;

    public UserFavItem(User user, Item item) {
        this.user = user;
        this.item = item;
    }
}
