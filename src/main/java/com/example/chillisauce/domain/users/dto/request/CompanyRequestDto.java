package com.example.chillisauce.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompanyRequestDto {
    private final String companyName;
    private final String certification;

    public CompanyRequestDto(SignupRequestDto request) {
        this.companyName = request.getCompanyName();
        this.certification = request.getCertification();
    }
}
