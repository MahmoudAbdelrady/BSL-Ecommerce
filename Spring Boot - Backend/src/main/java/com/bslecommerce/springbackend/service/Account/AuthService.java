package com.bslecommerce.springbackend.service.Account;

import com.bslecommerce.springbackend.dto.User.LoggedUserDTO;
import com.bslecommerce.springbackend.dto.User.LoginDTO;
import com.bslecommerce.springbackend.model.Account.Admin;
import com.bslecommerce.springbackend.model.Account.User;
import com.bslecommerce.springbackend.util.Response.ResponseMaker;
import com.bslecommerce.springbackend.util.SecretKeyReader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final SecretKeyReader secretKeyReader;

    @Autowired
    public AuthService(AuthenticationManager authManager, SecretKeyReader secretKeyReader) {
        this.authManager = authManager;
        this.secretKeyReader = secretKeyReader;
    }

    private String GenerateToken(UUID userId, String username, String userType) {
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMonths(1);
        Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .issuer("BSL")
                .subject("Token")
                .claim("id", userId)
                .claim("username", username)
                .claim("type", userType)
                .signWith(Keys.hmacShaKeyFor(secretKeyReader.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .issuedAt(new Date())
                .expiration(expiryDate)
                .compact();
    }

    public ResponseEntity<Object> login(LoginDTO loginDTO) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmailOrUsername(), loginDTO.getPassword());
            Authentication authentication = authManager.authenticate(authToken);
            LoggedUserDTO loggedUserDTO = new LoggedUserDTO();

            if (authentication.getPrincipal() instanceof User user) {
                loggedUserDTO.setUsername(user.getUsername());
                loggedUserDTO.setEmail(user.getEmail());
                loggedUserDTO.setToken(GenerateToken(user.getUserId(), user.getUsername(), "user"));
            } else if (authentication.getPrincipal() instanceof Admin admin) {
                loggedUserDTO.setUsername(admin.getUsername());
                loggedUserDTO.setEmail(admin.getEmail());
                loggedUserDTO.setToken(GenerateToken(null, admin.getUsername(), "admin"));
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(ResponseMaker.successRes("Logged in successfully", loggedUserDTO));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseMaker.errorRes("Invalid credentials"));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
