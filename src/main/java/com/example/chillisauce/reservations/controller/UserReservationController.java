package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.dto.response.UserReservationListResponse;
import com.example.chillisauce.reservations.service.UserReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserReservationController {
    private final UserReservationService userReservationService;

    /**
     * 회원의 예약 전체 조회
     */
    @GetMapping("/users/reservations")
    public ResponseEntity<ResponseMessage<UserReservationListResponse>> getUserReservations(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage
                .responseSuccess("회원의 예약 조회 성공", userReservationService.getUserReservations(userDetails));
    }
}
