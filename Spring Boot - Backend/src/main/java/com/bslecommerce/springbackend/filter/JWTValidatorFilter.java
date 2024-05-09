package com.bslecommerce.springbackend.filter;

import com.bslecommerce.springbackend.dto.User.UserInfo;
import com.bslecommerce.springbackend.util.SecretKeyReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JWTValidatorFilter extends OncePerRequestFilter {
    private final SecretKeyReader secretKeyReader;

    @Autowired
    public JWTValidatorFilter(SecretKeyReader secretKeyReader) {
        this.secretKeyReader = secretKeyReader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer")) {
            try {
                token = token.substring(7);
                SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyReader.getSecretKey().getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
                String userId = claims.get("id", String.class);
                String username = claims.get("username", String.class);
                String userType = claims.get("type", String.class);
                UserInfo userInfo = new UserInfo(userId, username, userType);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(userType));
                Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/login") || request.getServletPath().equals("/admin/login") || request.getServletPath().equals("/register");
    }
}
