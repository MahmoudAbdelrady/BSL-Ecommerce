package com.bslecommerce.springbackend.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private String userId;
    private String username;
    private String userType;
}
