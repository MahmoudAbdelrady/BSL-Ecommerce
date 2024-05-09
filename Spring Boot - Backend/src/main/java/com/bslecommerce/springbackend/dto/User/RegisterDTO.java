package com.bslecommerce.springbackend.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]{5,}$", message = "Username must be at least 5 characters long and contain only letters and numbers")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
