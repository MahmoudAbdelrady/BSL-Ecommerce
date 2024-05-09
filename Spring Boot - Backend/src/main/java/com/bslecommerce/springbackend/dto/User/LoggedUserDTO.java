package com.bslecommerce.springbackend.dto.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoggedUserDTO {
    private String username;
    private String email;
    private String token;
}
