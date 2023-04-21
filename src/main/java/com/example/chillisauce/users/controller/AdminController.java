package com.example.chillisauce.users.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.RoleDeptUpdateRequestDto;
import com.example.chillisauce.users.entity.UserRoleEnum;
import com.example.chillisauce.users.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /* 사원 목록 전체 조회 */
    @GetMapping("/admin/users")
    public ResponseEntity<ResponseMessage> getAllUsers (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("사원 전체 조회 성공",adminService.getAllUsers(userDetails));
    }

    /* 사원 선택 조회 */
    @GetMapping("/admin/users/{userId}")
    public ResponseEntity<ResponseMessage> getUsers (@PathVariable Long userId,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("사원 조회 성공", adminService.getUsers(userId, userDetails));
    }

    /* 사원 선택 조회 */
    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<ResponseMessage> editUser (@PathVariable Long userId,
                                                     @RequestBody RoleDeptUpdateRequestDto requestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("정보 수정 성공", adminService.editUser(userId, userDetails, requestDto));
    }
}
