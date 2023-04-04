package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 1개 회의실의 해당 날짜의 예약 타임 테이블 조회
     * 쿼리파라미터가 없으면 오늘 날짜의 예약 타임 테이블 조회
     */
    @GetMapping("/reservations/{meetingRoomId}")
    public ResponseEntity<ResponseMessage> getReservationTimetable(
            @RequestParam(value = "selDate", required = false, defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate selDate,
            @PathVariable Long meetingRoomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 조회 성공", reservationService.getReservationTimetable(selDate, meetingRoomId, userDetails));
    }

    /**
     * 회의실에 예약 등록
     */
    @PostMapping("/reservations/{meetingRoomId}")
    public ResponseEntity<ResponseMessage> addReservation(
            @PathVariable Long meetingRoomId,
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 등록 성공", reservationService.addReservation(meetingRoomId, requestDto, userDetails));
    }

    /**
     * 예약 수정
     */
    @PatchMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage> editReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 수정 성공", reservationService.editReservation(reservationId, requestDto, userDetails));
    }

    /**
     * 예약 삭제
     */
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage> deleteReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 삭제 성공", reservationService.deleteReservation(reservationId, userDetails));
    }
}
