package com.example.chillisauce.users.controller;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.service.EmailService;
import com.example.chillisauce.users.service.EmailServiceImpl;
import com.example.chillisauce.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;


    /* 이메일 인증 */
    @PostMapping("/users/signup/email")
    public String SendMail(@Valid @RequestBody SignupRequestDto request) throws Exception {
        return emailService.sendSimpleMessage(request.getEmail());
    }

    /* 관리자 회원가입 */
    @PostMapping("/users/signup/admin")
    public ResponseEntity<ResponseMessage> signupAdmin(@Valid @RequestBody SignupRequestDto request) {
        /**
         * 파라미터로 dto를 2개 사용이 안되기 때문에 트리구조를 빗대어 dto를 설계.
         * 현재 SignupRequest라는 dto에서 필요한 모든 값을 입력 받고,
         * AdminSignupRequestDto과 CompanyRequestDto에 각각 의존성을 생성자를 통해 주입해준다.
         */
        AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(request);
        CompanyRequestDto companyRequestDto = new CompanyRequestDto(request);

        return ResponseMessage.responseSuccess("관리자 회원가입 성공", userService.signupAdmin(adminSignupRequestDto, companyRequestDto));
    }

    /* 사원 회원가입 */
    @PostMapping("/users/signup/user")
    public ResponseEntity<ResponseMessage> signupUser(@Valid @RequestBody UserSignupRequestDto userSignupRequestDto) {

        return ResponseMessage.responseSuccess(userService.signupUser(userSignupRequestDto), "");
    }

    /* 로그인 */
    @PostMapping("/users/login")
    public ResponseEntity<ResponseMessage> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {

        return ResponseMessage.responseSuccess(userService.Login(loginRequestDto, response), "");
    }

    /* 토큰 재발급 */
    @GetMapping("/users/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        userService.refresh(request, response);
    }

    /* 인증번호 확인 */
    @PostMapping("/users/signup/match")
    public ResponseEntity<ResponseMessage> checkCertificationMatch(@RequestBody HashMap<String, String> certification) {
        return ResponseMessage.responseSuccess(userService.checkCertification(certification.get("certification")), "");
    }
}
