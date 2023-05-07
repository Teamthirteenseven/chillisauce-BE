package com.example.chillisauce.users.entity;

import com.example.chillisauce.users.dto.request.CompanyRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Companies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String companyName;

    @Column(unique = true, nullable = false)
    private String certification;

    public Companies(CompanyRequestDto companyRequestDto) {
        this.companyName = companyRequestDto.getCompanyName();
        this.certification = companyRequestDto.getCertification();
    }
}
