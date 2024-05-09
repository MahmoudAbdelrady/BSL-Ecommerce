package com.bslecommerce.springbackend.controller;

import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Object> GetAllCategories(@RequestParam("p") Integer page) throws Exception {
        return categoryService.getAllCategories(page);
    }

    @GetMapping("/{categoryId}/items")
    public ResponseEntity<Object> GetCategoryItems(@PathVariable UUID categoryId, @RequestParam("p") Integer page) throws Exception {
        return categoryService.getCategoryItems(categoryId, page);
    }
}
