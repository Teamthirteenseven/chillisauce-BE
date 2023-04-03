package com.example.chillisauce.users.dto;

import lombok.Getter;

@Getter
public class AdminSignupRequestDto {
    private String email;
    private String password;
    private String passwordCheck;
    private String userName;
    private String companyName; //회사명
}
