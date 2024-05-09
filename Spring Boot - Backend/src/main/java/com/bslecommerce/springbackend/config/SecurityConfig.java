package com.bslecommerce.springbackend.config;

import com.bslecommerce.springbackend.filter.JWTValidatorFilter;
import com.bslecommerce.springbackend.util.BSLECommerceAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
public class SecurityConfig {
    private final BSLECommerceAuthProvider bsleCommerceAuthProvider;

    @Autowired
    public SecurityConfig(BSLECommerceAuthProvider bsleCommerceAuthProvider) {
        this.bsleCommerceAuthProvider = bsleCommerceAuthProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JWTValidatorFilter jwtValidatorFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a -> {
                    a.requestMatchers("/admin/login", "/login", "/register").permitAll();
                    a.requestMatchers("/admin/**").hasAuthority("admin");
                    a.requestMatchers("/cart/**").hasAuthority("user");
                    a.requestMatchers(HttpMethod.POST, "/transactions", "/{transactionId}/cancel", "/reviews").hasAuthority("user");
                    a.requestMatchers(HttpMethod.DELETE, "/reviews/{reviewId}").hasAuthority("user");
                    a.anyRequest().authenticated();
                })
                .addFilterBefore(jwtValidatorFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(bsleCommerceAuthProvider));
    }
}
