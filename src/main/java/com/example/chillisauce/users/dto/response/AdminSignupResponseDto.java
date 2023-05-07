package com.example.chillisauce.users.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminSignupResponseDto {
    private final String certification;   //사업자 가입 완료 후 발급되는 인증번호의 역할

    public AdminSignupResponseDto(String certification) {
        this.certification = certification;
    }
}
