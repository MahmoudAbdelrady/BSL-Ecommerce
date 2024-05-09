package com.bslecommerce.springbackend.controller.Account;

import com.bslecommerce.springbackend.dto.User.LoginDTO;
import com.bslecommerce.springbackend.service.Account.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/login", "/admin/login"})
    public ResponseEntity<Object> Login(@RequestBody @Valid LoginDTO loginDTO) throws Exception {
        return authService.login(loginDTO);
    }
}
