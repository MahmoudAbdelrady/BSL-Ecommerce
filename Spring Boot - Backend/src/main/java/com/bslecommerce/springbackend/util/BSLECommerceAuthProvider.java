package com.bslecommerce.springbackend.util;

import com.bslecommerce.springbackend.service.Account.AdminService;
import com.bslecommerce.springbackend.service.Account.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BSLECommerceAuthProvider implements AuthenticationProvider {
    private final UserService userService;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpRequest;

    @Autowired
    public BSLECommerceAuthProvider(UserService userService, AdminService adminService, PasswordEncoder passwordEncoder, HttpServletRequest httpRequest) {
        this.userService = userService;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.httpRequest = httpRequest;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails;
        if (httpRequest.getServletPath().equals("/admin/login")) {
            userDetails = adminService.loadUserByUsername(username);
        } else {
            userDetails = userService.loadUserByUsername(username);
        }
        if (userDetails == null) throw new BadCredentialsException("Invalid credentials");
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, password);
        } else {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
