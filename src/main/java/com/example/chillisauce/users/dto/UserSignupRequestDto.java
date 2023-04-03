package com.example.chillisauce.users.dto;

import lombok.Getter;

@Getter
public class UserSignupRequestDto {
    private String email;
    private String password;
    private String passwordCheck;
    private String userName;
    private String certification;   //메일을 통해 받은 인증번호
}
