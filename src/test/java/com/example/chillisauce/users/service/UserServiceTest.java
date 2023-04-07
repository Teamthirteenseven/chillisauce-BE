package com.example.chillisauce.users.service;

import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @DisplayName("관리자 회원 가입")
    @Test
    void signupAdmin() {
        //given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .email("123@123")
                .password("1234")
                .passwordCheck("1234")
                .userName("루피")
                .companyName("원피스")
                .certification("123")
                .build();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(signupRequestDto.getPassword());
        //mocking company save
        Companies company = Companies.builder()
                .companyName(signupRequestDto.getCompanyName())
                .certification(signupRequestDto.getCertification())
                .build();
        Mockito.lenient().when(companyRepository.save(Mockito.any(Companies.class))).thenReturn(company);

        //mocking user save
        User saveAdmin = User.builder()
                .email(signupRequestDto.getEmail())
                .password(password)
                .username(signupRequestDto.getUserName())
                .role(UserRoleEnum.ADMIN)
                .companies(company)
                .build();
        Mockito.lenient().when(userRepository.save(Mockito.any(User.class))).thenReturn(saveAdmin);

        //when
        AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
        CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);
        AdminSignupResponseDto responseDto = userService.signupAdmin(adminSignupRequestDto, companyRequestDto);

        //then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getCertification()).isEqualTo(company.getCertification());

        //verify
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(any(String.class));
    }

    @DisplayName("사원 회원 가입")
    @Test
    void signupUser() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //given
        Companies company = Companies.builder()
                .companyName("원피스")
                .certification("123")
                .build();
        Mockito.when(companyRepository.findByCertification("123")).thenReturn(Optional.of(company));

        UserSignupRequestDto requestDto = UserSignupRequestDto.builder()
                .email("123@123")
                .password("1234")
                .passwordCheck("1234")
                .userName("루피")
                .certification("123")
                .build();

        String password = encoder.encode(requestDto.getPassword());

        User user = new User(requestDto, password, UserRoleEnum.USER, company);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        //when
        String result = userService.signupUser(requestDto);

        //then
        assertThat(userService.signupUser(requestDto)).isNotNull();
        assertThat("일반 회원 가입 성공").isEqualTo(result);

    }

    @DisplayName("로그인")
    @Test
    void login() {
        //given
        User saveAdmin = User.builder()
                .email("123@123")
                .password(passwordEncoder.encode("1234"))
                .username("루피")
                .role(UserRoleEnum.ADMIN)
                .companies(Companies.builder()
                        .companyName("원피스")
                        .certification("1234")
                        .build())
                .build();
        Mockito.when(userRepository.findByEmail("123@123")).thenReturn(Optional.of(saveAdmin));
//        Mockito.when(passwordEncoder.matches("1234", saveAdmin.getPassword())).thenReturn(true);

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("123@123")
                .password("1234")
                .build();

        //when
        LoginResponseDto loginResponseDto = userService.Login(loginRequestDto);

        //then
        assertThat(saveAdmin).isNotNull();
        assertThat(loginResponseDto.getEmail()).isEqualTo(saveAdmin.getEmail());
        assertThat(loginResponseDto.getUsername()).isEqualTo(saveAdmin.getUsername());

    }

    @DisplayName("로그인 실패(등록된 사용자가 없음)")
    @Test
    void fail1() {
        //given
        String email = "123@123";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password("1234")
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        UserException exception = assertThrows(UserException.class, () -> {
            userService.Login(loginRequestDto);
        });

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("로그인 실패(비밀번호 오류)")
    @Test
    void fail2() {
        //given
        String email = "123@123";
        String password = "1234";
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email(email)
                .password("5678")
                .build();

        User saveAdmin = User.builder()
                .email("123@123")
                .password(passwordEncoder.encode(password))
                .username("루피")
                .role(UserRoleEnum.ADMIN)
                .companies(Companies.builder()
                        .companyName("원피스")
                        .certification("1234")
                        .build())
                .build();
        Mockito.when(userRepository.findByEmail("123@123")).thenReturn(Optional.of(saveAdmin)); //가짜 저장

        //when
        UserException exception = assertThrows(UserException.class, () -> {
            userService.Login(loginRequestDto);
        });

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.NOT_PROPER_PASSWORD);

    }

//    @DisplayName("관리자 회원가입 실패(중복된 이메일)")
//    @Test
//    void fail3() {
//        //given
//        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
//                .email("123@123")
//                .password("1234")
//                .passwordCheck("1234")
//                .userName("루피")
//                .companyName("원피스")
//                .certification("123")
//                .build();
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String password = encoder.encode(signupRequestDto.getPassword());
//
//        Companies company = Companies.builder()
//                .companyName(signupRequestDto.getCompanyName())
//                .certification(signupRequestDto.getCertification())
//                .build();
//        Mockito.lenient().when(companyRepository.save(Mockito.any(Companies.class))).thenReturn(company);
//
//    }
//
//    @DisplayName("관리자 회원가입 실패(중복된 회사명)")
//    @Test
//    void fail4() {
//
//    }
//
//    @DisplayName("관리자 회원가입 실패(중복된 인증번호)")
//    @Test
//    void fail5() {
//
//    }
//
//    @DisplayName("관리자 회원가입 실패(비밀번호 일치 오류)")
//    @Test
//    void fail6() {
//
//    }
}