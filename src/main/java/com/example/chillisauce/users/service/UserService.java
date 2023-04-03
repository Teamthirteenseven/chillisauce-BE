package com.example.chillisauce.users.service;

import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final String CERTIFICATION = "admin";    //메일인증 구현 전까지 인증번호 역할.\
    private final PasswordEncoder passwordEncoder;

    //관리자 회원가입
    @Transactional
    public AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto) {
        //이메일 중복확인
        boolean found = userRepository.findByEmail(adminSignupRequestDto.getEmail()).isPresent();
        if (found) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        //회사명 중복확인
        found = userRepository.findByCompanyName(adminSignupRequestDto.getCompanyName()).isPresent();
        if (found) {
            throw new UserException(UserErrorCode.DUPLICATE_COMPANY);
        }
        //비밀번호 중복확인
        if (!adminSignupRequestDto.getPassword().equals(adminSignupRequestDto.getPasswordCheck())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }
        String password = adminSignupRequestDto.getPassword();
        //관리자 권한 부여
        UserRoleEnum role = UserRoleEnum.ADMIN;
        //관리자정보 저장
        userRepository.save(new User(adminSignupRequestDto, passwordEncoder.encode(password), role));

        return new AdminSignupResponseDto(CERTIFICATION);
    }

    // 일반사원 회원가입
    @Transactional
    public String signupUser(UserSignupRequestDto userSignupRequestDto) {
        //이메일 중복확인
        boolean found = userRepository.findByEmail(userSignupRequestDto.getEmail()).isPresent();
        if (found) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        //비밀번호 중복확인
        if (!userSignupRequestDto.getPassword().equals(userSignupRequestDto.getPasswordCheck())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }
        String password = userSignupRequestDto.getPassword();
        //일반 사원 권한 부여
        UserRoleEnum role = UserRoleEnum.USER;
        //사원정보 저장
        userRepository.save(new User(userSignupRequestDto, passwordEncoder.encode(password), role));
        return "일반 회원 가입 성공";
    }

    // 로그인
    @Transactional
    public LoginResponseDto Login(LoginRequestDto loginRequestDto) {
        // 사용자 확인
        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );
        //비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }

        return new LoginResponseDto(user, user.getRole());
    }

    // 유저 목록 전체조회

    // 유저 권한 수정

    //회사명 중복체크


}
