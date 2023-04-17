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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
//        found = companyRepository.findByCertification(companyRequestDto.getCertification()).isPresent();
//        if (found) {
//            throw new UserException(UserErrorCode.DUPLICATE_CERTIFICATION);
//        }

        //메일인증을 통해 발급받은 certification
//        String certification = emailServiceImpl.sendSimpleMessage(adminSignupRequestDto.getEmail()).substring(16);

        //회사 등록(회사이름, 회사 인증번호)
//        Companies createCompany = Companies.builder()
//                .companyName(companyRequestDto.getCompanyName())
//                .certification(certification)
//                .build();
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

        Companies company = companyRepository.findByCertification(userSignupRequestDto.getCertification()).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        //사원정보 저장
        userRepository.save(new User(userSignupRequestDto, passwordEncoder.encode(password), role, company));
        return "일반 회원 가입 성공";
    }

    //인증번호 일치여부 확인
    @Transactional
    public String checkCertification(String certification) {
        //사원이 입력한 인증번호로 해당 회사를 찾기 (일반 사원만 해당되는 것이라 구분을 어떻게 해야할지)
        companyRepository.findByCertification(certification).orElseThrow(
                () -> new UserException(UserErrorCode.INVALID_CERTIFICATION));

        return "인증번호가 확인 되었습니다.";
    }

    // 로그인
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
        TokenDto tokenDto = jwtUtil.createAllToken(loginRequestDto.getEmail());
        //리프레시 토큰 있는지 확인
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(loginRequestDto.getEmail());
        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), loginRequestDto.getEmail());
            refreshTokenRepository.save(newToken);
        }

//        Cookie accessTokenCookie = new Cookie(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setMaxAge((int) jwtUtil.getAccessTime());
//        accessTokenCookie.setPath("/");
//
//        Cookie refreshTokenCookie = new Cookie(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setMaxAge((int) jwtUtil.getRefreshTime());
//        refreshTokenCookie.setPath("/");

//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());

        return "로그인 성공";
    }

    @Transactional
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        // 클라이언트로부터 리프레시 토큰 가져오기
        String refreshToken = jwtUtil.getHeaderToken(request, "Refresh");
//        String refreshToken = jwtUtil.getHeaderTokenRefresh(request);

        // 리프레시 토큰 검증 및 DB와 일치하는지 확인
        if (jwtUtil.refreshTokenValidation(refreshToken)) {
            // 리프레시 토큰으로 이메일 정보 가져오기
            String email = jwtUtil.getUserInfoFromToken(refreshToken);

            // 새로운 엑세스 토큰 발급
            String newAccessToken = jwtUtil.createToken(email, "Access");

            // 헤더에 새로운 엑세스 토큰 설정
            jwtUtil.setHeaderAccessToken(response, newAccessToken);
        } else {
            // 리프레시 토큰이 유효하지 않거나 DB와 일치하지 않는 경우
            throw new UserException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }
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
