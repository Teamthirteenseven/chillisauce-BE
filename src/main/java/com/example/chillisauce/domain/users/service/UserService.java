package com.example.chillisauce.domain.users.service;

import com.example.chillisauce.domain.users.dto.request.AdminSignupRequestDto;
import com.example.chillisauce.domain.users.dto.request.LoginRequestDto;
import com.example.chillisauce.domain.users.dto.request.UserSignupRequestDto;
import com.example.chillisauce.domain.users.dto.response.AdminSignupResponseDto;
import com.example.chillisauce.domain.users.exception.UserErrorCode;
import com.example.chillisauce.domain.users.exception.UserException;
import com.example.chillisauce.domain.users.repository.CompanyRepository;
import com.example.chillisauce.domain.users.repository.UserRepository;
import com.example.chillisauce.domain.users.util.BaseUserFactory;
import com.example.chillisauce.global.security.jwt.JwtUtil;
import com.example.chillisauce.domain.users.dto.request.CompanyRequestDto;
import com.example.chillisauce.domain.users.entity.Companies;
import com.example.chillisauce.domain.users.entity.User;
import com.example.chillisauce.domain.users.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BaseUserFactory baseUserFactory;

    /* 관리자 회원 가입 */
    @Transactional
    public AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto, CompanyRequestDto companyRequestDto) {
        if (checkEmailDuplicate(adminSignupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        boolean checkedCompanyName = companyRepository.findByCompanyName(companyRequestDto.getCompanyName()).isPresent();
        if (checkedCompanyName) {
            throw new UserException(UserErrorCode.DUPLICATE_COMPANY);
        }

        boolean checkedCertification = companyRepository.findByCertification(companyRequestDto.getCertification()).isPresent();
        if (checkedCertification) {
            throw new UserException(UserErrorCode.DUPLICATE_CERTIFICATION);
        }

        Companies company = companyRepository.save(new Companies(companyRequestDto));

        String password = checkPasswordMatch(adminSignupRequestDto.getPassword(), adminSignupRequestDto.getPasswordCheck());

        UserRoleEnum role = UserRoleEnum.ADMIN;

        userRepository.save(new User(adminSignupRequestDto, passwordEncoder.encode(password), role, company));

        baseUserFactory.makeBaseUser(company.getCompanyName());

        return new AdminSignupResponseDto(company.getCertification());
    }

    /* 사원 회원 가입 */
    @Transactional
    public String signupUser(UserSignupRequestDto userSignupRequestDto) {
        if (checkEmailDuplicate(userSignupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }

        String password = checkPasswordMatch(userSignupRequestDto.getPassword(), userSignupRequestDto.getPasswordCheck());

        UserRoleEnum role = UserRoleEnum.USER;

        Companies company = companyRepository.findByCertification(userSignupRequestDto.getCertification()).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        userRepository.save(new User(userSignupRequestDto, passwordEncoder.encode(password), role, company));
        return "일반 회원 가입 성공";
    }

    /* 인증번호 일치여부 확인 */
    @Transactional
    public String checkCertification(String certification) {

        companyRepository.findByCertification(certification).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        return "인증번호가 확인 되었습니다.";
    }

    /* 로그인 */
    @Transactional
    public String Login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }


        String access = jwtUtil.createToken(user);

        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, access);

        return "로그인 성공";
    }

    /* 이메일 중복확인 */
    private boolean checkEmailDuplicate(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    /* 비밀번호 일치여부 확인 */
    private String checkPasswordMatch(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }
        return password;
    }

}
