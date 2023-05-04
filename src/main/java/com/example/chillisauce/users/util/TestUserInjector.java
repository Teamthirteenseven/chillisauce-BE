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
    private final PasswordEncoder passwordEncoder;

    // 관리자 가입 시 해당 회사에 5명의 테스트 유저 가입시키기
    public void injectUsers(String companyName) throws UserException {
        Companies company = companyRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new UserException(UserErrorCode.COMPANY_NOT_FOUND));

        List<String> userInformationList = List.of("홍길동 test1@test" + company.getId() + ".com",
                "채소연 test2@test" + company.getId() + ".com",
                "김철수 test3@test" + company.getId() + ".com",
                "윤대협 test4@test" + company.getId() + ".com",
                "성춘향 test5@test" + company.getId() + ".com");
        List<User> userList = userInformationList.stream().map(x -> {
                    String[] str = x.split(" ");
                    return User.builder()
                            .username(str[0])
                            .role(UserRoleEnum.USER)
                            .companies(company)
                            .password(passwordEncoder.encode("1q2w3e4r!"))
                            .email(str[1])
                            .build();
                }
        ).toList();
        userRepository.saveAll(userList);
    }
}