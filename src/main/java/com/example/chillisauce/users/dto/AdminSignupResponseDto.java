package com.example.chillisauce.users.dto;

import lombok.Getter;

@Getter
public class AdminSignupResponseDto {
    private String certification;   //사업자 가입 완료 후 발급되는 인증번호의 역할

    public AdminSignupResponseDto(String certification) {
        this.certification = certification;
    }
}
