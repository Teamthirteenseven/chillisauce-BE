package com.example.chillisauce.users.dto;

import lombok.Getter;

@Getter
public class AdminSignupRequestDto {
    private final String email;
    private final String password;
    private final String passwordCheck;
    private final String userName;

    public AdminSignupRequestDto(SignupRequestDto request) {
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.passwordCheck = request.getPasswordCheck();
        this.userName = request.getUserName();
    }

}
