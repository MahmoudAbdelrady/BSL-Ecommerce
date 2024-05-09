package com.bslecommerce.springbackend.service;

import com.bslecommerce.springbackend.dto.CategoryDTO;
import com.bslecommerce.springbackend.dto.Item.ItemDTO;
import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.model.Category;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.repository.CategoryRepository;
import com.bslecommerce.springbackend.repository.Item.ItemRepository;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<Object> getAllCategories(Integer idx) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<Category> categories = categoryRepository.findAll(pageRequest).getContent();
            List<CategoryDTO> categoryDTOS = categories.stream().map(cat -> {
                cat.setItems(null);
                return modelMapper.map(cat, CategoryDTO.class);
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Retrieved all categories successfully", categoryDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> getCategoryItems(UUID catId, Integer idx) throws Exception {
        try {
            Category category = categoryRepository.findCategoryByCatId(catId);
            if (category != null) {
                category.setItems(null);
                CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
                PageRequest pageRequest = PageRequest.of(idx, 10);
                List<Item> items = itemRepository.findItemByCatId(catId, pageRequest).getContent();
                List<ItemDTO> itemDTOS = items.stream().map(item -> {
                    item.setReviews(null);
                    ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);
                    itemDTO.setCategory(item.getCategory().getTitle());
                    return itemDTO;
                }).toList();
                categoryDTO.setItems(itemDTOS);
                return ResponseEntity.ok(ResponseMaker.successRes("Category retrieved successfully", categoryDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
