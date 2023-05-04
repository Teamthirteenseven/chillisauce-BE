package com.example.chillisauce.users.service;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import com.example.chillisauce.users.util.TestUserInjector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.example.chillisauce.fixture.FixtureFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 클래스")
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private TestUserInjector testUserInjector;
    @Mock
    private JwtUtil jwtUtil;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    private Companies company = Company_생성();
    private User user = User_USER권한_생성(company);
    private User admin = User_ADMIN권한_생성(company, "gurwlstm1210@gmail.com");

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {
        @DisplayName("관리자 회원 가입")
        @Test
        void signupAdmin() {
            //given
            SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .companyName("원피스")
                    .certification("123")
                    .build();

            //when
            when(companyRepository.save(any())).thenReturn(company);
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);
            AdminSignupResponseDto result = userService.signupAdmin(adminSignupRequestDto, companyRequestDto);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getCertification()).isEqualTo(company.getCertification());

            verify(userRepository, times(1)).save(any(User.class));
        }

        @DisplayName("사원 회원 가입")
        @Test
        void signupUser() {
            //given
            UserSignupRequestDto requestDto = UserSignupRequestDto.builder()
                    .email("123@123")
                    .password("12345678")
                    .passwordCheck("12345678")
                    .username("루피")
                    .certification(Company_생성().getCertification())
                    .build();

            //when
            Mockito.when(companyRepository.findByCertification(requestDto.getCertification())).thenReturn(Optional.of(company));
            String result = userService.signupUser(requestDto);

            //then
            assertThat(result).isNotNull();
            assertThat("일반 회원 가입 성공").isEqualTo(result);

        }

        @DisplayName("로그인")
        @Test
        void login() {
            Map<String, String> headers = new HashMap<>();
            HttpServletResponse response = mock(HttpServletResponse.class);

            //가짜 헤더 생성
            doAnswer(invocation -> {
                String key = invocation.getArgument(0);
                String value = invocation.getArgument(1);
                headers.put(key, value);
                return null;
            }).when(response).setHeader(anyString(), anyString());


            //given
            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email(user.getEmail())
                    .password("12345678")
                    .build();

            String fakeAccess = "fakeAccess";

            Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
            Mockito.when(jwtUtil.createToken(user)).thenReturn(fakeAccess);

            //when
            String result = userService.Login(loginRequestDto, response);
            String accessToken = headers.get(JwtUtil.AUTHORIZATION_HEADER);
            //then
            assertThat(user).isNotNull();
            assertThat("로그인 성공").isEqualTo(result);
            assertThat(accessToken).isNotEmpty();

        }

        @DisplayName("인증번호 확인 성공")
        @Test
        void certification() {
            //given
            when(companyRepository.findByCertification(company.getCertification())).thenReturn(Optional.of(company));

            //when
            userService.checkCertification(company.getCertification());

            //then
            assertThat(company).isNotNull();
            assertThat(company.getCertification()).isEqualTo(company.getCertification());
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCase {
        @DisplayName("로그인 실패(등록된 사용자가 없음)")
        @Test
        void fail1() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);

            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .build();

            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.Login(loginRequestDto, response);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("등록된 사용자가 없습니다");
        }

        @DisplayName("로그인 실패(비밀번호 오류)")
        @Test
        void fail2() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);

            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email(admin.getEmail())
                    .password("5678")
                    .build();

            when(userRepository.findByEmail(any())).thenReturn(Optional.of(admin)); //가짜 저장

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.Login(loginRequestDto, response);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");

        }


        @DisplayName("관리자 회원가입 실패(중복된 이메일)")
        @Test
        void fail3() {
            //given
            SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                    .email("123@124")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .companyName("원피스")
                    .certification("123")
                    .build();

            when(userRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.of(admin));

            //when
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);

            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupAdmin(adminSignupRequestDto, companyRequestDto);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("중복된 이메일이 존재합니다");
        }

        @DisplayName("관리자 회원가입 실패(중복된 회사명)")
        @Test
        void fail4() {
            //given
            SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                    .email("123@124")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .companyName("원피스")
                    .certification("123")
                    .build();

            when(companyRepository.findByCompanyName(signupRequestDto.getCompanyName())).thenReturn(Optional.of(Companies.builder().build()));
            //when
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);

            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupAdmin(adminSignupRequestDto, companyRequestDto);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("중복된 회사명이 존재합니다");
        }

        @DisplayName("관리자 회원가입 실패(비밀번호 일치 오류)")
        @Test
        void fail6() {
            //given
            SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                    .email("123@124")
                    .password("1234")
                    .passwordCheck("5678")
                    .username("루피")
                    .companyName("원피스")
                    .certification("123")
                    .build();
            //when
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);

            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupAdmin(adminSignupRequestDto, companyRequestDto);
            });

            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
        }

        @DisplayName("사원 회원가입 실패(중복된 이메일)")
        @Test
        void fail7() {
            //given
            UserSignupRequestDto requestDto = UserSignupRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .certification("123")
                    .build();

            when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(User.builder().build()));

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupUser(requestDto);
            });

            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("중복된 이메일이 존재합니다");
        }

        @DisplayName("사원 회원가입 실패(비밀번호 일치 오류)")
        @Test
        void fail8() {
            //given
            UserSignupRequestDto requestDto = UserSignupRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .passwordCheck("5678")
                    .username("루피")
                    .certification("123")
                    .build();

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupUser(requestDto);
            });
            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
        }

        @DisplayName("회원가입 실패(유효하지 않은 인증번호)")
        @Test
        void fail9() {
            //given
            String certification = "123";

            when(companyRepository.findByCertification(certification)).thenReturn(Optional.empty());

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.checkCertification(certification);
            });

            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("인증번호가 유효하지 않습니다");
        }

        @DisplayName("관리자 회원가입 실패(중복된 인증번호 사용)")
        @Test
        void fail10() {
            //given
            SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .companyName("원피스")
                    .certification("testCert")
                    .build();
            String cert = "testCert";
            when(companyRepository.findByCertification(cert)).thenReturn(Optional.of(company));
            //when
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);
            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupAdmin(adminSignupRequestDto, companyRequestDto);
            });
            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("이미 사용중인 인증번호 입니다.");
        }

        @DisplayName("사원 회원가입 실패(유효하지 않은 인증번호)")
        @Test
        void fail11() {
            //given
            UserSignupRequestDto requestDto = UserSignupRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .passwordCheck("1234")
                    .username("루피")
                    .certification("123")
                    .build();
            when(companyRepository.findByCertification(requestDto.getCertification())).thenReturn(Optional.empty());
            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupUser(requestDto);
            });
            //then
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("인증번호가 유효하지 않습니다");
        }
    }
}