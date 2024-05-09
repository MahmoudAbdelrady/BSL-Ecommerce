package com.bslecommerce.springbackend.repository.Account;

import com.bslecommerce.springbackend.model.Account.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findAdminByUsernameOrEmail(String username, String email);
}
