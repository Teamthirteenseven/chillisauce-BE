package com.example.chillisauce.domain.users.repository;

import com.example.chillisauce.global.config.TestConfig;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
@Nested
@DisplayName("Companies Test")
class CompanyRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    @DisplayName("회사 생성 성공")
    @Test
    void addCompany() {
        //given
        final Companies companies = Companies.builder()
                .companyName("7jo")
                .certification("1234")
                .build();

        //when
        Companies saveCompanies = companyRepository.save(companies);

        //then
        assertThat(saveCompanies.getId()).isNotNull();
        assertThat(saveCompanies.getCompanyName()).isEqualTo("7jo");
        assertThat(saveCompanies.getCertification()).isEqualTo("1234");
    }

    @DisplayName("회사명이 존재하는지 확인")
    @Test
    void findCompanyName() {
        //given
        final Companies companies = Companies.builder()
                .companyName("7jo")
                .certification("1234")
                .build();

        //when
        companyRepository.save(companies);
        Optional<Companies> findCompany = companyRepository.findByCompanyName("7jo");

        //then
        assertThat(findCompany).isNotNull();
        assertThat(findCompany.get().getId()).isNotNull();
        assertThat(findCompany.get().getCompanyName()).isEqualTo("7jo");
        assertThat(findCompany.get().getCertification()).isEqualTo("1234");
    }

    @DisplayName("인증번호가 존재하는지 확인")
    @Test
    void CertificationMatched() {
        //given
        final Companies companies = Companies.builder()
                .companyName("7jo")
                .certification("1234")
                .build();
        //when
        companyRepository.save(companies);
        Optional<Companies> CheckCertification = companyRepository.findByCertification("1234");

        //then
        assertThat(CheckCertification).isNotNull();
        assertThat(CheckCertification.get().getId()).isNotNull();
        assertThat(CheckCertification.get().getCompanyName()).isEqualTo("7jo");
        assertThat(CheckCertification.get().getCertification()).isEqualTo("1234");
    }

}