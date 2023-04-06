package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.dto.CompanyRequestDto;
import com.example.chillisauce.users.dto.SignupRequestDto;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Nested
@DisplayName("Companies Test")
class CompanyRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;


    private Companies company() {
        return Companies.builder()
                .id(1L)
                .companyName("7jo")
                .certification("1234")
                .build();
    }

    private Companies company2() {
        return Companies.builder()
                .id(2L)
                .companyName("7jo")
                .certification("5678")
                .build();
    }

    @DisplayName("회사 생성 성공")
    @Test
    void addCompany() {
        //given
        Companies companies = company();

        //when
        Companies saveCompanies = companyRepository.save(companies);

        //then
        assertThat(saveCompanies.getId()).isNotNull();
        assertThat(saveCompanies.getCompanyName()).isEqualTo(companies.getCompanyName());
        assertThat(saveCompanies.getCertification()).isEqualTo(companies.getCertification());
    }

    @DisplayName("회사명이 존재하는지 확인")
    @Test
    void findCompanyName() {
        //given
        Companies companies = company();

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
        Companies companies = company();

        //when
        companyRepository.save(companies);
        Optional<Companies> CheckCertification = companyRepository.findByCertification("1234");

        //then
        assertThat(CheckCertification).isNotNull();
        assertThat(CheckCertification.get().getId()).isNotNull();
        assertThat(CheckCertification.get().getCompanyName()).isEqualTo("7jo");
        assertThat(CheckCertification.get().getCertification()).isEqualTo("1234");
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Nested
        @DisplayName("회사명")
        class companyName {
//            @DisplayName("중복된 회사명")
//            @Test
//            void fail1() {
//                // given
//                final Companies companies = Companies.builder()
//                        .companyName("7jo")
//                        .certification("1234")
//                        .build();
//                final Companies companies2 = Companies.builder()
//                        .companyName("7jo")
//                        .certification("5678")
//                        .build();
//
//                // when
//                companyRepository.save(companies);
//
//                // then
//                final Throwable thrown = catchThrowable(() -> companyRepository.save(companies2));
//                assertThat(thrown).isInstanceOf(UserException.class)
//                        .hasMessage(UserErrorCode.DUPLICATE_COMPANY.getMessage());
//            }

            @DisplayName("회사명이 Null인 경우")
            @Test
            void fail2() {
                // given
                final Companies companies = Companies.builder()
                        .companyName(null)
                        .certification("1234")
                        .build();
                //when
                assertThrows(DataIntegrityViolationException.class,
                        () -> companyRepository.save(companies));
                //then

            }
        }

    }

}