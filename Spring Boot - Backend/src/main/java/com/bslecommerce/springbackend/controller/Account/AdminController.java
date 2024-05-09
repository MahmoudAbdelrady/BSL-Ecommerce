package com.bslecommerce.springbackend.controller.Account;

import com.bslecommerce.springbackend.dto.CategoryDTO;
import com.bslecommerce.springbackend.dto.Item.ItemDTO;
import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionOpDTO;
import com.bslecommerce.springbackend.service.Account.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<Object> GetAllUsers(@RequestParam("p") Integer page) throws Exception {
        return adminService.getAllUsers(page);
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> CreateCategory(@RequestPart("category_data") @Valid CategoryDTO categoryDTO,
                                                 @RequestPart("category_photo") MultipartFile categoryPhoto) throws Exception {
        return adminService.saveCategory(categoryDTO, categoryPhoto);
    }

    @PutMapping("/categories")
    public ResponseEntity<Object> EditCategory(@RequestPart("category_data") @Valid CategoryDTO categoryDTO,
                                               @RequestPart(value = "category_photo", required = false) MultipartFile categoryPhoto) throws Exception {
        return adminService.editCategory(categoryDTO, categoryPhoto);
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<Object> DeleteCategoryById(@PathVariable UUID categoryId) throws Exception {
        return adminService.deleteCategoryById(categoryId);
    }

    @PutMapping("/categories/{categoryId}/items")
    public ResponseEntity<Object> CategoryOPItem(@RequestBody ItemOpDTO itemOpDTO, @PathVariable UUID categoryId) throws Exception {
        return adminService.categoryOpItem(itemOpDTO, categoryId);
    }

    @PostMapping("/items")
    public ResponseEntity<Object> CreateItem(@RequestPart("item_data") @Valid ItemDTO itemDTO,
                                             @RequestPart("item_photo") MultipartFile itemPhoto) throws Exception {
        return adminService.createItem(itemDTO, itemPhoto);
    }

    @PutMapping("/items")
    public ResponseEntity<Object> EditItem(@RequestPart("item_data") @Valid ItemDTO itemDTO,
                                           @RequestPart(value = "item_photo", required = false) MultipartFile itemPhoto) throws Exception {
        return adminService.editItem(itemDTO, itemPhoto);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Object> DeleteItemById(@PathVariable UUID itemId) throws Exception {
        return adminService.deleteItemById(itemId);
    }

    @PostMapping("/transactions/actions")
    public ResponseEntity<Object> TransactionAction(@RequestBody @Valid TransactionOpDTO transactionOpDTO) throws Exception {
        return adminService.transactionOp(transactionOpDTO);
    }
}
