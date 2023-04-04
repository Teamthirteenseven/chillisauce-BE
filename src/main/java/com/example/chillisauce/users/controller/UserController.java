package com.example.chillisauce.users.controller;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping( "/users/signup/admin")
    public ResponseEntity<ResponseMessage> signupAdmin(@RequestBody SignupRequestDto request) {
        /**
         * 파라미터로 dto를 2개 사용이 안되기 때문에 트리구조를 빗대어 dto를 설계.
         * 현재 SignupRequest라는 dto에서 필요한 모든 값을 입력 받고,
         * AdminSignupRequestDto과 CompanyRequestDto에 각각 의존성을 생성자를 통해 주입해준다.
         */
        AdminSignupRequestDto adminSignupRequestDto = new AdminSignupRequestDto(request);
        CompanyRequestDto companyRequestDto = new CompanyRequestDto(request);

        return ResponseMessage.responseSuccess("관리자 회원가입 성공", userService.signupAdmin(adminSignupRequestDto, companyRequestDto));
    }

    @PostMapping("/users/signup/user")
    public ResponseEntity<ResponseMessage> signupUser(@RequestBody UserSignupRequestDto userSignupRequestDto) {

        return ResponseMessage.responseSuccess(userService.signupUser(userSignupRequestDto), "");
    }

    @PostMapping("/users/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto user = userService.Login(loginRequestDto);
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getId(), user.getRole()));
        return ResponseMessage.responseSuccess("로그인 성공", "");
    }
}
