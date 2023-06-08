package com.example.chillisauce.domain.users.controller;

import com.example.chillisauce.domain.users.dto.request.*;
import com.example.chillisauce.domain.users.dto.response.AdminSignupResponseDto;
import com.example.chillisauce.domain.users.service.EmailService;
import com.example.chillisauce.domain.users.service.UserService;
import com.example.chillisauce.global.message.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ResponseMessage<String>> SendMail(@Valid @RequestBody HashMap<String, String> email) throws Exception {
        return ResponseMessage.responseSuccess("이메일을 발송하였습니다", emailService.sendSimpleMessage(email.get("email")));
    }

    /* 관리자 회원가입 */
    @PostMapping("/users/signup/admin")
    public ResponseEntity<ResponseMessage<AdminSignupResponseDto>> signupAdmin(@Valid @RequestBody SignupRequestDto request) {

        AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(request);
        CompanyRequestDto companyRequestDto = new CompanyRequestDto(request);

        return ResponseMessage.responseSuccess("관리자 회원가입 성공", userService.signupAdmin(adminSignupRequestDto, companyRequestDto));
    }

    /* 사원 회원가입 */
    @PostMapping("/users/signup/user")
    public ResponseEntity<ResponseMessage<String>> signupUser(@Valid @RequestBody UserSignupRequestDto userSignupRequestDto) {

        return ResponseMessage.responseSuccess(userService.signupUser(userSignupRequestDto), "");
    }

    /* 로그인 */
    @PostMapping("/users/login")
    public ResponseEntity<ResponseMessage<String>> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {

        return ResponseMessage.responseSuccess(userService.Login(loginRequestDto, response), "");
    }

    /* 인증번호 확인 */
    @PostMapping("/users/signup/match")
    public ResponseEntity<ResponseMessage<String>> checkCertificationMatch(@RequestBody HashMap<String, String> certification) {
        return ResponseMessage.responseSuccess(userService.checkCertification(certification.get("certification")), "");
    }
}
