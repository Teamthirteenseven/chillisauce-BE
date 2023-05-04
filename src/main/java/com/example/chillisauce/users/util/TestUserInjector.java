package com.example.chillisauce.users.util;

import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestUserInjector {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // 관리자 가입 시 해당 회사에 5명의 테스트 유저 가입시키기
    public void injectUsers(String companyName) throws UserException {
        Companies company = companyRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new UserException(UserErrorCode.COMPANY_NOT_FOUND));

        /* 테스트 1. 관리자 회원가입 시, 유저 100만명을 추가 생성한다. */
        List<User> testUserList = new ArrayList<>();
        for (int i = 1; i <= 1000000; i++) {
            String username = i + "번 사용자";
            String email = "test" + i + "@test" + company.getId() + ".com";

            User user = User.builder()
                    .email(email)
                    .username(username)
                    .role(UserRoleEnum.USER)
                    .password("1234qwer!")
                    .companies(company)
                    .build();

            testUserList.add(user);
        }

        userRepository.saveAll(testUserList);
    }

}