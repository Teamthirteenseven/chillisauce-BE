package com.example.chillisauce.users.dto.response;

import com.example.chillisauce.users.entity.Companies;
import lombok.Getter;

@Getter
public class CompanyResponseDto {
    private final String companyName;
    private final String certification;

    public CompanyResponseDto(Companies companies) {
        this.companyName = companies.getCompanyName();
        this.certification = companies.getCertification();
    }
}
