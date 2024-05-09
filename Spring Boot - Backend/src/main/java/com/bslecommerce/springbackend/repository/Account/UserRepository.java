package com.bslecommerce.springbackend.repository.Account;

import com.bslecommerce.springbackend.model.Account.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUserId(UUID userId);

    User findUserByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u JOIN FETCH u.cart WHERE u.username=:username")
    User fetchUserWithCartByUsername(@Param("username") String username);
}
