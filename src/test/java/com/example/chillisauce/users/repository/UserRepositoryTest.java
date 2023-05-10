package com.example.chillisauce.users.repository;

import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@Nested
@DisplayName("users Test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @DisplayName("관리자 추가")
    @Test
    void addAdmin() {
        //given
        Companies companies = Companies.builder()
                .companyName("호그와트")
                .certification("1234")
                .build();
        User user = User.builder()
                .email("123@123")
                .password("1234")
                .username("덤블도어")
                .role(UserRoleEnum.ADMIN)
                .companies(companies)
                .build();

        //when
        User saveAdmin = userRepository.save(user);

        //then
        assertThat(saveAdmin.getId()).isEqualTo(user.getId());
        assertThat(saveAdmin.getEmail()).isEqualTo(user.getEmail());
        assertThat(saveAdmin.getPassword()).isEqualTo(user.getPassword());
        assertThat(saveAdmin.getUsername()).isEqualTo(user.getUsername());
        assertThat(saveAdmin.getRole()).isEqualTo(user.getRole());
        assertThat(saveAdmin.getCompanies()).isNotNull();
    }

    @DisplayName("사원 추가")
    @Test
    void addUser() {
        //given
        Companies companies = Companies.builder()
                .companyName("7jo")
                .certification("1234")
                .build();
        User user = User.builder()
                .email("123@123")
                .password("1234")
                .username("해리포터")
                .role(UserRoleEnum.USER)
                .companies(companies)
                .build();

        //when
        User saveUser = userRepository.save(user);

        //then
        assertThat(saveUser.getId()).isEqualTo(user.getId());
        assertThat(saveUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(saveUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(saveUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(saveUser.getRole()).isEqualTo(user.getRole());
        assertThat(saveUser.getCompanies()).isNotNull();
    }

}