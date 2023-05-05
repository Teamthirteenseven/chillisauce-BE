package com.example.chillisauce.performance.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.performance.service.PerformanceService;
import com.example.chillisauce.performance.dto.ReservationInjectRequest;
import com.example.chillisauce.performance.dto.ScheduleInjectRequest;
import com.example.chillisauce.performance.dto.SpaceInjectRequest;
import com.example.chillisauce.performance.dto.UserInjectRequest;
import com.example.chillisauce.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;
    @PostMapping("/users/inject")
    public ResponseEntity<ResponseMessage<String>> injectUsers(@RequestBody UserInjectRequest request,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage
                .responseSuccess("성능 테스트용 유저 생성 성공", performanceService.injectUsers(request, userDetails));
    }

    @PostMapping("/spaces/inject")
    public ResponseEntity<ResponseMessage<String>> injectSpaces(@RequestBody SpaceInjectRequest request,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage
                .responseSuccess("성능 테스트용 스페이스 생성 성공", performanceService.injectSpaces(request, userDetails));
    }

    @PostMapping("/reservations/inject")
    public ResponseEntity<ResponseMessage<String>> injectReservations(@RequestBody ReservationInjectRequest request,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage
                .responseSuccess("성능 테스트용 예약 생성 성공", performanceService.injectReservations(request, userDetails));
    }

    @PostMapping("/schedules/inject")
    public ResponseEntity<ResponseMessage<String>> injectSchedules(@RequestBody ScheduleInjectRequest request,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseMessage
                .responseSuccess("성능 테스트용 일정 생성 성공", performanceService.injectSchedules(request, userDetails));
    }
}
