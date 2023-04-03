package com.example.chillisauce.users.dto;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final String email;
    private final String username;
    private final UserRoleEnum role;

    public LoginResponseDto(User user, UserRoleEnum role) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = role;
    }
}
