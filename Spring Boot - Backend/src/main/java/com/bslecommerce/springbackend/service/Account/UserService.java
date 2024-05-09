package com.bslecommerce.springbackend.service.Account;

import com.bslecommerce.springbackend.dto.ReviewDTO;
import com.bslecommerce.springbackend.dto.Transaction.TransactionDTO;
import com.bslecommerce.springbackend.dto.User.RegisterDTO;
import com.bslecommerce.springbackend.dto.User.UserDTO;
import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.model.Cart.Cart;
import com.bslecommerce.springbackend.model.Review;
import com.bslecommerce.springbackend.model.Transaction;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.repository.ReviewRepository;
import com.bslecommerce.springbackend.repository.TransactionRepository;
import com.bslecommerce.springbackend.repository.Account.UserRepository;
import com.bslecommerce.springbackend.util.FirebaseUploader;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseUploader firebaseUploader;

    @Autowired
    public UserService(UserRepository userRepository, ReviewRepository reviewRepository, TransactionRepository transactionRepository,
                       ModelMapper modelMapper, PasswordEncoder passwordEncoder, FirebaseUploader firebaseUploader) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.reviewRepository = reviewRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.firebaseUploader = firebaseUploader;
    }

    public ResponseEntity<Object> getUserProfile() throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user != null) {
                user.setReviews(null);
                user.setTransactions(null);

                PageRequest pageRequest = PageRequest.of(0, 5);
                List<Review> reviews = reviewRepository.findReviewsByUserId(user.getUserId(), pageRequest).getContent();
                List<Transaction> transactions = transactionRepository.findAllByUserId(user.getUserId(), pageRequest).getContent();

                List<ReviewDTO> reviewDTOS = reviews.stream().map(review -> {
                    review.setUser(null);
                    ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
                    reviewDTO.setItemId(review.getItem().getItemId());
                    reviewDTO.setItemName(review.getItem().getTitle());
                    reviewDTO.setItemPhoto(review.getItem().getPhoto());
                    reviewDTO.setPhoto(user.getPhoto());
                    reviewDTO.setUsername(user.getUsername());
                    return reviewDTO;
                }).toList();

                List<TransactionDTO> transactionDTOS = transactions.stream().map(transaction -> {
                    transaction.setCartItems(null);
                    return modelMapper.map(transaction, TransactionDTO.class);
                }).toList();

                UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                userDTO.setReviews(reviewDTOS);
                userDTO.setTransactions(transactionDTOS);

                return ResponseEntity.ok(ResponseMaker.successRes("User retrieved successfully", userDTO));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> saveUser(RegisterDTO registerDTO, MultipartFile userPhoto) throws Exception {
        try {
            User existingUser = userRepository.findUserByUsernameOrEmail(registerDTO.getUsername(), registerDTO.getEmail());
            if (existingUser != null) {
                if (existingUser.getUsername().equals(registerDTO.getUsername())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Username is already in use."));
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseMaker.errorRes("Email is already in use."));
            } else {
                if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMaker.errorRes("Passwords must match"));
                }
                registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
                User user = modelMapper.map(registerDTO, User.class);
                if (userPhoto != null) {
                    String photoUrl = firebaseUploader.uploadPhoto(userPhoto, "users");
                    user.setPhoto(photoUrl);
                }
                Cart cart = new Cart();
                user.setCart(cart);
                cart.setUser(user);
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMaker.successRes("Account created successfully", null));
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public ResponseEntity<Object> editUser(UserDTO userDTO, MultipartFile userPhoto) throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
            }
            user.setFirstname(userDTO.getFirstname());
            user.setLastname(userDTO.getLastname());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            if (userPhoto != null) {
                firebaseUploader.deletePhoto(user.getPhoto(), "users");
                String photoUrl = firebaseUploader.uploadPhoto(userPhoto, "users");
                user.setPhoto(photoUrl);
            }
            user = userRepository.save(user);
            user.setReviews(null);
            user.setTransactions(null);
            modelMapper.map(user, userDTO);
            return ResponseEntity.ok(ResponseMaker.successRes("User updated successfully", userDTO));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public ResponseEntity<Object> deleteUserById() throws Exception {
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findUserByUserId(UUID.fromString(userInfo.getUserId()));
            if (user != null) {
                firebaseUploader.deletePhoto(user.getPhoto(), "users");
                userRepository.delete(user);
                return ResponseEntity.ok(ResponseMaker.successRes("User deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMaker.errorRes("User not found"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsernameOrEmail(username, username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User not found");
    }
}
