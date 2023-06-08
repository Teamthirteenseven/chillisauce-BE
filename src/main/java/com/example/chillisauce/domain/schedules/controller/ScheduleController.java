package com.example.chillisauce.domain.schedules.controller;

import com.example.chillisauce.domain.schedules.dto.ScheduleTimetableResponseDto;
import com.example.chillisauce.global.message.ResponseMessage;
import com.example.chillisauce.domain.schedules.dto.ScheduleListResponseDto;
import com.example.chillisauce.domain.schedules.dto.ScheduleRequestDto;
import com.example.chillisauce.domain.schedules.dto.ScheduleResponseDto;
import com.example.chillisauce.domain.schedules.service.ScheduleService;
import com.example.chillisauce.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 당일 스케줄 조회
     */
    @GetMapping("/schedules")
    public ResponseEntity<ResponseMessage<ScheduleTimetableResponseDto>> getDaySchedules(
            @RequestParam(value = "selDate", required = false, defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate selDate,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("당일 스케줄 조회 성공", scheduleService.getDaySchedules(selDate, userDetails));
    }

    /**
     * 개인 전체 스케줄 조회
     */
    @GetMapping("/schedules/all")
    public ResponseEntity<ResponseMessage<ScheduleListResponseDto>> getAllSchedules(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("개인 전체 스케줄 조회 성공", scheduleService.getAllSchedules(userDetails));
    }
    /**
     * 개인 스케줄 등록
     */
    @PostMapping("/schedules")
    public ResponseEntity<ResponseMessage<ScheduleResponseDto>> addSchedule(
            @RequestBody @Valid ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("스케줄 등록 성공", scheduleService.addSchedule(requestDto, userDetails));
    }

    /**
     * 스케줄 수정
     */
    @PatchMapping("/schedules/{scId}")
    public ResponseEntity<ResponseMessage<ScheduleResponseDto>> editSchedule(
            @PathVariable Long scId,
            @RequestBody ScheduleRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("스케줄 수정 성공", scheduleService.editSchedule(scId, requestDto, userDetails));
    }

    /**
     * 스케줄 삭제
     */
    @DeleteMapping("/schedules/{scId}")
    public ResponseEntity<ResponseMessage<String>> deleteSchedule(
            @PathVariable Long scId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("스케줄 삭제 성공", scheduleService.deleteSchedule(scId, userDetails));
    }
}
