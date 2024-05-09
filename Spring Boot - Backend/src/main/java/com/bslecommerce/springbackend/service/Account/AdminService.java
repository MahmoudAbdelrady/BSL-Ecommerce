package com.bslecommerce.springbackend.service.Account;

import com.bslecommerce.springbackend.dto.CategoryDTO;
import com.bslecommerce.springbackend.dto.Item.ItemDTO;
import com.bslecommerce.springbackend.dto.Item.ItemOpDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionOpDTO;
import com.bslecommerce.springbackend.dto.User.UserDTO;
import com.bslecommerce.springbackend.model.Account.Admin;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.model.Category;
import com.bslecommerce.springbackend.model.Item.Item;
import com.bslecommerce.springbackend.model.Item.UserPurItem;
import com.bslecommerce.springbackend.model.Transaction;
import com.bslecommerce.springbackend.repository.Account.AdminRepository;
import com.bslecommerce.springbackend.repository.Account.UserRepository;
import com.bslecommerce.springbackend.repository.CategoryRepository;
import com.bslecommerce.springbackend.repository.Item.ItemRepository;
import com.bslecommerce.springbackend.repository.Item.UserFavItemRepository;
import com.bslecommerce.springbackend.repository.Item.UserPurItemRepository;
import com.bslecommerce.springbackend.repository.TransactionRepository;
import com.bslecommerce.springbackend.util.FirebaseUploader;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import com.bslecommerce.springbackend.util.TransactionStatusMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final UserFavItemRepository favItemRepository;
    private final UserPurItemRepository purItemRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final TransactionStatusMapper transMapper;
    private final FirebaseUploader firebaseUploader;

    @Autowired
    public AdminService(AdminRepository adminRepository, UserRepository userRepository, CategoryRepository categoryRepository
            , ItemRepository itemRepository, TransactionRepository transactionRepository, UserFavItemRepository favItemRepository, ModelMapper modelMapper,
                        UserPurItemRepository purItemRepository, TransactionStatusMapper transMapper, FirebaseUploader firebaseUploader) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.favItemRepository = favItemRepository;
        this.purItemRepository = purItemRepository;
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
        this.transMapper = transMapper;
        this.firebaseUploader = firebaseUploader;
    }

    public ResponseEntity<Object> getAllUsers(Integer idx) throws Exception {
        try {
            PageRequest pageRequest = PageRequest.of(idx, 10);
            List<User> users = userRepository.findAll(pageRequest).getContent();
            List<UserDTO> userDTOS = users.stream().map(user -> {
                user.setReviews(null);
                user.setTransactions(null);
                return modelMapper.map(user, UserDTO.class);
            }).toList();
            return ResponseEntity.ok(ResponseMaker.successRes("Retrieved all users successfully", userDTOS));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> saveCategory(CategoryDTO categoryDTO, MultipartFile categoryPhoto) throws Exception {
        try {
            Category existingCategory = categoryRepository.findCategoryByTitleIgnoreCase(categoryDTO.getTitle());
            if (existingCategory != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Category already exists."));
            }
            Category category = modelMapper.map(categoryDTO, Category.class);
            String photoUrl = firebaseUploader.uploadPhoto(categoryPhoto, "categories");
            category.setPhoto(photoUrl);
            categoryRepository.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMaker.successRes("Category created successfully", null));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> editCategory(CategoryDTO categoryDTO, MultipartFile categoryPhoto) throws Exception {
        try {
            Category category = categoryRepository.findCategoryByCatId(categoryDTO.getCatId());
            if (category != null) {
                Category existingCategory = categoryRepository.findCategoryByTitleIgnoreCase(categoryDTO.getTitle());
                if (existingCategory != null && !existingCategory.getCatId().equals(categoryDTO.getCatId())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Category already exists."));
                }
                if (categoryPhoto != null) {
                    firebaseUploader.deletePhoto(category.getPhoto(), "categories");
                    String photoUrl = firebaseUploader.uploadPhoto(categoryPhoto, "categories");
                    category.setPhoto(photoUrl);
                }
                category.setTitle(categoryDTO.getTitle());
                category = categoryRepository.save(category);
                modelMapper.map(category, categoryDTO);
                return ResponseEntity.ok(ResponseMaker.successRes("Category updated successfully", categoryDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> deleteCategoryById(UUID catId) throws Exception {
        try {
            Category category = categoryRepository.findCategoryByCatId(catId);
            if (category != null) {
                itemRepository.updateItemsCategoryToNullByCategory(category);
                firebaseUploader.deletePhoto(category.getPhoto(), "categories");
                categoryRepository.delete(category);
                return ResponseEntity.ok(ResponseMaker.successRes("Category deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> categoryOpItem(ItemOpDTO itemOpDTO, UUID catId) throws Exception {
        try {
            Category category = categoryRepository.findCategoryByCatId(catId);
            if (category != null) {
                Item item = itemRepository.findItemByItemId(itemOpDTO.getItemId());
                if (item != null) {
                    if (itemOpDTO.getAction().equals("add")) {
                        boolean isAdded = category.AddItemToCategory(item);
                        if (isAdded) {
                            item.setCategory(category);
                            categoryRepository.save(category);
                            return ResponseEntity
                                    .ok(ResponseMaker.successRes("Item added successfully to " + category.getTitle() + " category", null));
                        }
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(ResponseMaker.errorRes("Item already exists in " + category.getTitle() + " category"));
                    }
                    boolean isRemoved = category.RemoveItemFromCategory(item);
                    if (isRemoved) {
                        item.setCategory(null);
                        categoryRepository.save(category);
                        return ResponseEntity
                                .ok(ResponseMaker.successRes("Item removed successfully from " + category.getTitle() + " category", null));
                    }
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ResponseMaker.errorRes("Item doesn't exist in " + category.getTitle() + " category"));
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> createItem(ItemDTO itemDTO, MultipartFile itemPhoto) throws Exception {
        try {
            Item existingItem = itemRepository.findItemByTitleIgnoreCase(itemDTO.getTitle());
            if (existingItem != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Item already exists"));
            }
            Item item = modelMapper.map(itemDTO, Item.class);
            if (itemDTO.getCategory() != null) {
                Category category = categoryRepository.findCategoryByTitleIgnoreCase(itemDTO.getCategory());
                if (category == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
                }
                category.AddItemToCategory(item);
                item.setCategory(category);
                itemDTO.setCategory(category.getTitle());
            }
            String photoUrl = firebaseUploader.uploadPhoto(itemPhoto, "items");
            item.setPhoto(photoUrl);
            item = itemRepository.save(item);
            item.setCategory(null);
            modelMapper.map(item, itemDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMaker.successRes("Item created successfully", itemDTO));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> editItem(ItemDTO itemDTO, MultipartFile itemPhoto) throws Exception {
        try {
            Item item = itemRepository.fetchItemById(itemDTO.getItemId());
            if (item != null) {
                if (itemDTO.getCategory() != null) {
                    Category category = categoryRepository.findCategoryByTitleIgnoreCase(itemDTO.getCategory());
                    if (category == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Category not found"));
                    }
                    item.setCategory(category);
                }
                item.setTitle(itemDTO.getTitle());
                item.setPrice(itemDTO.getPrice());
                item.setQuantity(itemDTO.getQuantity());
                item.setDescription(itemDTO.getDescription());
                if (itemPhoto != null) {
                    firebaseUploader.deletePhoto(item.getPhoto(), "items");
                    String photoUrl = firebaseUploader.uploadPhoto(itemPhoto, "items");
                    item.setPhoto(photoUrl);
                }
                item = itemRepository.save(item);
                item.setCategory(null);
                item.setReviews(null);
                modelMapper.map(item, itemDTO);
                return ResponseEntity.ok(ResponseMaker.successRes("Item updated successfully", itemDTO));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity<Object> deleteItemById(UUID itemId) throws Exception {
        try {
            Item item = itemRepository.findItemByItemId(itemId);
            if (item != null) {
                favItemRepository.deleteFavItemsByItemId(item.getItemId());
                purItemRepository.deletePurItemByItemId(item.getItemId());
                firebaseUploader.deletePhoto(item.getPhoto(), "items");
                itemRepository.delete(item);
                return ResponseEntity.ok(ResponseMaker.successRes("Item deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Item not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> transactionOp(TransactionOpDTO transactionOpDTO) throws Exception {
        try {
            Transaction transaction = transactionRepository.findByTransactionId(transactionOpDTO.getTransactionId());
            if (transaction != null) {
                User user = transaction.getUser();
                if (transaction.getStatus() == 3) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ResponseMaker.successRes("Transaction already done, Can't change status.", null));
                }
                if (!transMapper.getStatusToInt().containsKey(transactionOpDTO.getStatus())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Invalid status"));
                }
                if (transMapper.getStatusToInt().get(transactionOpDTO.getStatus()) == 0 && transaction.getStatus() >= 1) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ResponseMaker.successRes("Can't unconfirm a transaction.", null));
                }
                if (transMapper.getStatusToInt().get(transactionOpDTO.getStatus()) == 3) {
                    transaction.getCartItems().forEach(cartItem -> {
                        Item item = cartItem.getItem();
                        UserPurItem userPurItem = new UserPurItem(user, item);
                        user.AddItem(user.getPurchasedItems(), userPurItem);
                    });
                }
                transaction.setStatus(transMapper.getStatusToInt().get(transactionOpDTO.getStatus()));
                transaction = transactionRepository.save(transaction);
                TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
                transactionDTO.setStatus(transactionOpDTO.getStatus());
                return ResponseEntity.ok(ResponseMaker.successRes("Transaction status updated successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("Transaction not found"));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findAdminByUsernameOrEmail(username, username);
        if (admin == null) {
            throw new UsernameNotFoundException("Admin not found");
        }
        return admin;
    }
}
