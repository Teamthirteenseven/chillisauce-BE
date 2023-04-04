package com.example.chillisauce.users.service;

import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    //관리자 회원가입
    @Transactional
    public AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto, CompanyRequestDto companyRequestDto) {
        //이메일 중복확인
        if (checkEmailDuplicate(adminSignupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
        //회사명 중복확인
        boolean found = companyRepository.findByCompanyName(companyRequestDto.getCompanyName()).isPresent();
        if (found) {
            throw new UserException(UserErrorCode.DUPLICATE_COMPANY);
        }
        //인증번호 중복확인(이메일인증 완료되면 에러코드와 같이 삭제할 것.)
        found = companyRepository.findByCertification(companyRequestDto.getCertification()).isPresent();
        if (found) {
            throw new UserException(UserErrorCode.DUPLICATE_CERTIFICATION);
        }

        //회사 등록(회사이름, 회사 인증번호)
        Companies company = companyRepository.save(new Companies(companyRequestDto));

        //비밀번호 중복확인
        String password = checkPasswordMatch(adminSignupRequestDto.getPassword(), adminSignupRequestDto.getPasswordCheck());
        //관리자 권한 부여
        UserRoleEnum role = UserRoleEnum.ADMIN;
        //관리자정보 저장
        userRepository.save(new User(adminSignupRequestDto, passwordEncoder.encode(password), role, company));

        return new AdminSignupResponseDto(company.getCertification());
    }

    //일반사원 회원가입
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
        //회사 인증번호와 입력한 인증번호 매치 여부 확인
        Companies company = companyRepository.findByCertification(userSignupRequestDto.getCertification()).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        //사원정보 저장
        userRepository.save(new User(userSignupRequestDto, passwordEncoder.encode(password), role, company));
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

    //이메일중복확인
    private boolean checkEmailDuplicate(String email) {

        return userRepository.findByEmail(email).isPresent();
    }

    //비밀번호 중복확인
    private String checkPasswordMatch(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }
        return password;
    }

}
