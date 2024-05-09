package com.bslecommerce.springbackend.repository;

import com.bslecommerce.springbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryByCatId(UUID catId);
    Category findCategoryByTitleIgnoreCase(String title);
}
