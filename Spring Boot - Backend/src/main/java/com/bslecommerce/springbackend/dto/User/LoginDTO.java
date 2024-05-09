package com.bslecommerce.springbackend.dto.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    @NotBlank(message = "Email or Username is required")
    private String emailOrUsername;
    @NotBlank(message = "Password is required")
    private String password;
}
