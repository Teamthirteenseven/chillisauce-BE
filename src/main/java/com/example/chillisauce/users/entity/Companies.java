package com.example.chillisauce.users.entity;

import com.example.chillisauce.users.dto.CompanyRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
/* 성능테스트 1. 인덱싱 / 논인덱싱 비교*/
//@Table(name = "companies", indexes = {
//        @Index(name = "idx_companyName", columnList = "companyName", unique = true)
//})
/* 성능테스트 1. 인덱싱 / 논인덱싱 비교*/
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
