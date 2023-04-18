package com.example.chillisauce.users.dto;

import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class UserDetailResponseDto {
    Long userId;
    String email;
    String username;
    UserRoleEnum role;

    public UserDetailResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
