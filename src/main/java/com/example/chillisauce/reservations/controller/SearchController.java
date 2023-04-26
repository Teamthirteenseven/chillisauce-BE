package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.service.SearchService;
import com.example.chillisauce.security.UserDetailsImpl;
import com.example.chillisauce.users.dto.UserDetailResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/users/search")
    public ResponseEntity<ResponseMessage<List<UserDetailResponseDto>>> searchUser(
            @RequestParam(value = "name") String name,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("유저 검색 성공", searchService.searchUser(name, userDetails));
    }
}
