package com.bslecommerce.springbackend.controller;

import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<Object> GetAllItems(@RequestParam("p") Integer page) throws Exception {
        return itemService.getAllItems(page);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> GetItemById(@PathVariable UUID itemId) throws Exception {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/fav")
    public ResponseEntity<Object> GetFavItems(@RequestParam("p") Integer page) throws Exception {
        return itemService.getFavItems(page);
    }

    @PostMapping("/fav")
    public ResponseEntity<Object> ItemOpFavItems(@RequestBody @Valid ItemOpDTO itemOpDTO) throws Exception {
        return itemService.itemOpFavItems(itemOpDTO);
    }
}
