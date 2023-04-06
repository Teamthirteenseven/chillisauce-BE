package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;


    @DisplayName("회사 추가")
    @Test
    void addCompany() {
        //given
        Companies companies = companies();

        //when
        Companies saveCompany = companyRepository.save(companies);

        //then
        assertThat(saveCompany.getId()).isEqualTo(companies.getId());
        assertThat(saveCompany.getCompanyName()).isEqualTo(companies.getCompanyName());
        assertThat(saveCompany.getCertification()).isEqualTo(companies.getCertification());
    }
    private Companies companies () {
        return Companies.builder()
                .id(1L)
                .companyName("7jo")
                .certification("1234")
                .build();
    }
    @DisplayName("사용자 추가")
    @Test
    void addUser() {
        //given
        User user = user();

        //when
        User saveUser = userRepository.save(user);

        //then
        assertThat(saveUser.getId()).isEqualTo(user.getId());
        assertThat(saveUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(saveUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(saveUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(saveUser.getRole()).isEqualTo(user.getRole());
//        assertThat(saveUser.getCompanies().getId()).isEqualTo(user.getCompanies().getId());
//        assertThat(saveUser.getCompanies().getCompanyName()).isEqualTo(user.getCompanies().getCompanyName());
//        assertThat(saveUser.getCompanies().getCertification()).isEqualTo(user.getCompanies().getCertification());
    }
    private User user () {
        return User.builder()
                .id(1L)
                .email("123@123")
                .password("1234")
                .username("바니바니")
                .role(UserRoleEnum.ADMIN)
//                .companies(companies())
                .build();
    }

}