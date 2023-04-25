package com.example.chillisauce.users.service;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.RefreshToken;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.RefreshTokenRepository;
import com.example.chillisauce.users.repository.UserRepository;
import com.example.chillisauce.users.util.TestUserInjector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private TestUserInjector testUserInjector;
    @Mock
    private JwtUtil jwtUtil;
    @Spy
    private BCryptPasswordEncoder passwordEncoder;


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
                    .username(signupRequestDto.getUsername())
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
                    .username("루피")
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
            Map<String, String> headers = new HashMap<>();
            HttpServletResponse response = mock(HttpServletResponse.class);

            //가짜 헤더 생성
            doAnswer(invocation -> {
                String key = invocation.getArgument(0);
                String value = invocation.getArgument(1);
                headers.put(key, value);
                return null;
            }).when(response).addHeader(anyString(), anyString());


            //given
            User saveAdmin = User.builder()
                    .email("123@123")
                    .password(passwordEncoder.encode("1234qwer!"))
                    .username("루피")
                    .role(UserRoleEnum.ADMIN)
                    .companies(Companies.builder()
                            .companyName("원피스")
                            .certification("1234")
                            .build())
                    .build();
            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email("123@123")
                    .password("1234qwer!")
                    .build();

            String fakeAccess = "fakeAccess";
            String fakeRefresh = "fakeRefresh";

            TokenDto tokenDto = new TokenDto(fakeAccess, fakeRefresh);

            Mockito.when(userRepository.findByEmail("123@123")).thenReturn(Optional.of(saveAdmin));
            Mockito.when(jwtUtil.createAllToken(loginRequestDto.getEmail())).thenReturn(tokenDto);
            Mockito.when(refreshTokenRepository.findByEmail("123@123")).thenReturn(Optional.empty());

            //when
            String result = userService.Login(loginRequestDto, response);

            //then
            assertThat(saveAdmin).isNotNull();
            assertThat("로그인 성공").isEqualTo(result);

            String accessToken = headers.get(JwtUtil.AUTHORIZATION_HEADER);
            String refreshToken = headers.get(JwtUtil.REFRESH_TOKEN);

            assertThat(accessToken).isNotEmpty();
            assertThat(refreshToken).isNotEmpty();

        }

        @DisplayName("리프레시토큰 저장 성공")
        @Test
        void refreshToken() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);
            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email("123@123")
                    .password("1234")
                    .build();
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


            TokenDto tokenDto = TokenDto.builder()
                    .accessToken("fake")
                    .refreshToken("fakeRefresh")
                    .build();

            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(tokenDto.getRefreshToken())
                    .email("123@123")
                    .build();
            Mockito.when(userRepository.findByEmail("123@123")).thenReturn(Optional.of(saveAdmin));
            Mockito.when(jwtUtil.createAllToken(loginRequestDto.getEmail())).thenReturn(tokenDto);
            Mockito.when(refreshTokenRepository.findByEmail("123@123")).thenReturn(Optional.of(refreshToken));
            Mockito.when(refreshTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            //when
            String result = userService.Login(loginRequestDto, response);
            System.out.println(tokenDto.getAccessToken());
            System.out.println(tokenDto.getRefreshToken());

            //then
            assertThat(saveAdmin).isNotNull();
            assertThat("로그인 성공").isEqualTo(result);

            //???????????????
            ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
            Mockito.verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
            RefreshToken updatedRefreshToken = refreshTokenCaptor.getValue();

            assertThat(updatedRefreshToken).isNotNull();
            assertThat(updatedRefreshToken.getEmail()).isEqualTo(refreshToken.getEmail());
            assertThat(updatedRefreshToken.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());

        }

        @DisplayName("새로운 엑세스토큰 발급 성공")
        @Test
        void newAccessToken() {
            //given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String refreshToken = "fakeRefresh";
            String email = "123@123";
            String newAccessToken = "fakeNewAccess";

            when(jwtUtil.getHeaderToken(request, "Refresh")).thenReturn(refreshToken);
            when(jwtUtil.refreshTokenValidation(refreshToken)).thenReturn(true);
            when(jwtUtil.getUserInfoFromToken(refreshToken)).thenReturn(email);
            when(jwtUtil.createToken(email, "Access")).thenReturn(newAccessToken);

            //when
            userService.refresh(request, response);

            //then
            assertThat(newAccessToken).isNotNull();
            verify(jwtUtil).setHeaderAccessToken(response, newAccessToken);

        }

        @DisplayName("인증번호 확인 성공")
        @Test
        void certification() {
            //given
            String certification = "1234";

            Companies companies = Companies.builder()
                    .companyName("원피스")
                    .certification("1234")
                    .build();

            when(companyRepository.findByCertification("1234")).thenReturn(Optional.of(companies));

            //when
            userService.checkCertification(certification);

            //then
            assertThat(companies).isNotNull();
            assertThat(companies.getCertification()).isEqualTo(certification);
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
            String email = "123@123";

            LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                    .email(email)
                    .password("1234")
                    .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            /**
             * userRepository.findByEmail(email)메서드를 이용해 요청을 보내면,
             * thenReturn(Optional.empty())을 통해 empty를 리턴한다.
             */

            //when
            UserException exception = assertThrows(UserException.class, () -> {
                userService.Login(loginRequestDto, response);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("등록된 사용자가 없습니다");
        }

        @DisplayName("로그인 실패(비밀번호 오류)")
        @Test
        void fail2() {
            //given
            HttpServletResponse response = mock(HttpServletResponse.class);
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
                userService.Login(loginRequestDto, response);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");

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
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = encoder.encode(signupRequestDto.getPassword());

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

            when(userRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.of(saveAdmin));

            //when
            AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(signupRequestDto);
            CompanyRequestDto companyRequestDto = new CompanyRequestDto(signupRequestDto);

            UserException exception = assertThrows(UserException.class, () -> {
                userService.signupAdmin(adminSignupRequestDto, companyRequestDto);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getMessage()).isEqualTo("중복된 이메일이 존재합니다");
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
            assertThat(exception.getMessage()).isEqualTo("중복된 회사명이 존재합니다");
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
            assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
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
            assertThat(exception.getMessage()).isEqualTo("중복된 이메일이 존재합니다");
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
            assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
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
            assertThat(exception.getMessage()).isEqualTo("인증번호가 유효하지 않습니다");
        }

        @DisplayName("새로운 엑세스토큰 발급 실패")
        @Test
        void fail11() {
            //given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            String refreshToken = "fakeRefresh";

            when(jwtUtil.getHeaderToken(request, "Refresh")).thenReturn(refreshToken);
            when(jwtUtil.refreshTokenValidation(refreshToken)).thenReturn(false);   //검증을 통과하지 못한 상황

            //when
            UserException exception = assertThrows(UserException.class,
                    () -> userService.refresh(request, response));

            //then
            assertThat(exception.getMessage()).isEqualTo("리프레시 토큰이 유효하지 않습니다");
        }

//        @DisplayName("새로운 엑세스토큰 발급 실패(리프레시토큰이 Null 인 경우)")
//        @Test
//        void fail12() {
//            //given
//            HttpServletRequest request = mock(HttpServletRequest.class);
//            HttpServletResponse response = mock(HttpServletResponse.class);
//
//            when(jwtUtil.getHeaderToken(request, JwtUtil.REFRESH_TOKEN)).thenReturn(String.valueOf(Optional.empty()));
//
//            //when
//            UserException exception = assertThrows(UserException.class,
//                    () -> userService.refresh(request, response));
//
//            //then
//            assertThat(exception.getMessage()).isEqualTo("리프레시 토큰이 유효하지 않습니다");
//        }

    }
}