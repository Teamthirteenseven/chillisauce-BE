package com.example.chillisauce.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminSignupRequestDto {
    private final String email;
    private final String password;
    private final String passwordCheck;
    private final String username;

    public AdminSignupRequestDto(SignupRequestDto request) {
        this.email = request.getEmail();
        this.password = request.getPassword();
        this.passwordCheck = request.getPasswordCheck();
        this.username = request.getUsername();
    }

}
