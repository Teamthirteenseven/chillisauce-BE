package com.example.chillisauce.performance.controller;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.performance.dto.SuperuserRequest;
import com.example.chillisauce.users.entity.Companies;
import com.example.chillisauce.users.entity.User;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import com.example.chillisauce.users.repository.CompanyRepository;
import com.example.chillisauce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SuperuserController {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Value("${superuser.certification}")
    private String suCertification;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/users/signup/superuser")
    public ResponseEntity<ResponseMessage<String>> signupSuperuser(@RequestBody SuperuserRequest request) {
        if (!request.getCert().equals(suCertification)) {
            throw new UserException(UserErrorCode.NOT_PROPER_CERTIFICATION);
        }

        Companies suCompany = companyRepository.save(Companies.builder().companyName("suCompany").certification("xx").build());

        userRepository.save(User.builder()
                .role(UserRoleEnum.SUPERUSER)
                .email(request.getUsername())
                .companies(suCompany)
                .password(passwordEncoder.encode(request.getPassword()))
                .username("superuser")
                .build());

        return ResponseMessage
                .responseSuccess("개발자용 유저 회원가입 성공", "");
    }

    @PostMapping("/users/login/superuser")
    public ResponseEntity<ResponseMessage<String>> loginSuperuser(@RequestBody SuperuserRequest request,
                                                                  HttpServletResponse response) {
        // superuser 인증번호 확인
        if (!request.getCert().equals(suCertification)) {
            throw new UserException(UserErrorCode.NOT_PROPER_CERTIFICATION);
        }

        // 사용자 확인
        User user = userRepository.findByEmail(request.getUsername()).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );

        //비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.NOT_PROPER_PASSWORD);
        }

        // 이메일 정보로 토큰 생성
        String access = jwtUtil.createToken(user);

        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, access);

        return ResponseMessage
                .responseSuccess("개발자용 유저 로그인 성공", "");
    }
}
