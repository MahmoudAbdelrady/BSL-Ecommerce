package com.bslecommerce.springbackend.controller.Account;

import com.bslecommerce.springbackend.dto.User.RegisterDTO;
import com.bslecommerce.springbackend.dto.User.UserDTO;
import com.bslecommerce.springbackend.service.Account.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/profile")
    public ResponseEntity<Object> GetUserProfile() throws Exception {
        return userService.getUserProfile();
    }

    @PostMapping("/register")
    public ResponseEntity<Object> Register(@RequestPart("register_data") @Valid RegisterDTO registerDTO,
                                           @RequestPart(value = "user_photo", required = false) MultipartFile userPhoto) throws Exception {
        return userService.saveUser(registerDTO, userPhoto);
    }

    @PutMapping("/users")
    public ResponseEntity<Object> EditUser(@RequestPart("user_data") @Valid UserDTO userDTO,
                                           @RequestPart(value = "user_photo", required = false) MultipartFile userPhoto) throws Exception {
        return userService.editUser(userDTO, userPhoto);
    }

    @DeleteMapping("/users")
    public ResponseEntity<Object> DeleteUserById() throws Exception {
        return userService.deleteUserById();
    }
}
