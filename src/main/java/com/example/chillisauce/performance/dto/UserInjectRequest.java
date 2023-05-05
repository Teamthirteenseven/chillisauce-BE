package com.example.chillisauce.performance.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserInjectRequest {
    String email;
    String password;
    String passwordCheck;
    String username;
    Integer count;
}
