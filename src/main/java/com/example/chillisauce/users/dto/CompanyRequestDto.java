package com.example.chillisauce.users.dto;

import lombok.Getter;

@Getter
public class CompanyRequestDto {
    private final String companyName;
    private final String certification;

    public CompanyRequestDto(SignupRequestDto request) {
        this.companyName = request.getCompanyName();
        this.certification = request.getCertification();
    }
}
