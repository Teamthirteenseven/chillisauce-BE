package com.example.chillisauce.users.controller;

import com.example.chillisauce.jwt.JwtUtil;
import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.users.dto.*;
import com.example.chillisauce.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

    @PostMapping("/users/signup/admin")
    public ResponseEntity signupAdmin (@RequestBody AdminSignupRequestDto adminSignupRequestDto) {
        return ResponseMessage.responseSuccess("관리자 회원가입 성공", userService.signupAdmin(adminSignupRequestDto));
    }

    @PostMapping("/users/signup/user")
    public ResponseEntity signupUser (@RequestBody UserSignupRequestDto userSignupRequestDto) {
        return ResponseMessage.responseSuccess(userService.signupUser(userSignupRequestDto),"");
    }

    @PostMapping("/users/login")
    public ResponseEntity login (@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto user = userService.Login(loginRequestDto);
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getEmail(), user.getRole()));
        return ResponseMessage.responseSuccess("로그인 성공", "");
    }
}
