package com.example.chillisauce.users.service;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.users.dto.request.AdminSignupRequestDto;
import com.example.chillisauce.users.dto.request.CompanyRequestDto;
import com.example.chillisauce.users.dto.request.LoginRequestDto;
import com.example.chillisauce.users.dto.request.UserSignupRequestDto;
import com.example.chillisauce.users.dto.response.AdminSignupResponseDto;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import com.example.chillisauce.users.util.TestUserInjector;
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
    private final TestUserInjector testUserInjector;

    /* 관리자 회원 가입 */
    @Transactional
    public AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto, CompanyRequestDto companyRequestDto) {
        //이메일 중복확인
        if (checkEmailDuplicate(adminSignupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        //회사명 중복확인
        boolean checkedCompanyName = companyRepository.findByCompanyName(companyRequestDto.getCompanyName()).isPresent();
        if (checkedCompanyName) {
            throw new UserException(UserErrorCode.DUPLICATE_COMPANY);
        }

        boolean checkedCertification = companyRepository.findByCertification(companyRequestDto.getCertification()).isPresent();
        if (checkedCertification) {
            throw new UserException(UserErrorCode.DUPLICATE_CERTIFICATION);
        }

        Companies company = companyRepository.save(new Companies(companyRequestDto));

        //비밀번호 중복확인
        String password = checkPasswordMatch(adminSignupRequestDto.getPassword(), adminSignupRequestDto.getPasswordCheck());
        //관리자 권한 부여
        UserRoleEnum role = UserRoleEnum.ADMIN;
        //관리자정보 저장
        userRepository.save(new User(adminSignupRequestDto, passwordEncoder.encode(password), role, company));

        testUserInjector.injectUsers(company.getCompanyName());

        return new AdminSignupResponseDto(company.getCertification());
    }

    /* 사원 회원 가입 */
    @Transactional
    public String signupUser(UserSignupRequestDto userSignupRequestDto) {
        //이메일 중복확인
        if (checkEmailDuplicate(userSignupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        //비밀번호 일치여부 확인
        String password = checkPasswordMatch(userSignupRequestDto.getPassword(), userSignupRequestDto.getPasswordCheck());
        //일반 사원 권한 부여
        UserRoleEnum role = UserRoleEnum.USER;

        Companies company = companyRepository.findByCertification(userSignupRequestDto.getCertification()).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        //사원정보 저장
        userRepository.save(new User(userSignupRequestDto, passwordEncoder.encode(password), role, company));
        return "일반 회원 가입 성공";
    }

    /* 인증번호 일치여부 확인 */
    @Transactional
    public String checkCertification(String certification) {
        //사원이 입력한 인증번호로 해당 회사를 찾기 (일반 사원만 해당되는 것이라 구분을 어떻게 해야할지)
        companyRepository.findByCertification(certification).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        return "인증번호가 확인 되었습니다.";
    }

    /* 로그인 */
    @Transactional
    public String Login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // 사용자 확인
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );
        //비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }

        // 이메일 정보로 토큰 생성
        String access = jwtUtil.createToken(user);

        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, access);

        return "로그인 성공";
    }

    /* 회원 정보 수정 */


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
