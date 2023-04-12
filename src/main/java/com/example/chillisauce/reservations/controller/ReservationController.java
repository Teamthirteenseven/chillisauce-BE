package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.dto.ReservationRequestDto;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "예약 API", description = "예약 도메인의 API 명세서입니다.")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 전체 예약 조회
     */
    @Operation(summary = "전체 예약 조회",
            description = "특정 회의실의 특정 날짜 예약 내역을 타임단위로 조회합니다.")
    @GetMapping("/reservations/{companyName}/all")
    public ResponseEntity<ResponseMessage> getAllReservations(
            @Parameter(description = "회사 이름", required = true, example = "testCompany")
            @PathVariable String companyName,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("전체 예약 조회 성공", reservationService.getAllReservations(companyName, userDetails));
    }

    /**
     * 1개 회의실의 해당 날짜의 예약 타임 테이블 조회
     * 쿼리파라미터가 없으면 오늘 날짜의 예약 타임 테이블 조회
     */
    @Operation(summary = "예약 타임테이블 조회",
            description = "특정 회의실의 특정 날짜 예약 내역을 타임단위로 조회합니다.")
    @GetMapping("/reservations/{meetingRoomId}")
    public ResponseEntity<ResponseMessage> getReservationTimetable(
            @Parameter(description = "선택날짜", example = "2023-04-10")
            @RequestParam(value = "selDate", required = false, defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selDate,
            @Parameter(description = "회의실 id 값", required = true, example = "3")
            @PathVariable Long meetingRoomId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 조회 성공", reservationService.getReservationTimetable(selDate, meetingRoomId, userDetails));
    }

    /**
     * 회의실에 예약 등록
     */
    @Operation(summary = "예약 등록",
            description = "특정 회의실에 예약을 등록합니다. DB에 등록된 회의실의 id값이 필요합니다.")
    @PostMapping("/reservations/{meetingRoomId}")
    public ResponseEntity<ResponseMessage> addReservation(
            @Parameter(description = "회의실 id 값", required = true, example = "3")
            @PathVariable Long meetingRoomId,
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 등록 성공", reservationService.addReservation(meetingRoomId, requestDto, userDetails));
    }

    /**
     * 예약 수정
     */
    @Operation(summary = "예약 수정",
            description = "회원 자신이 등록한 예약을 수정합니다. DB에 등록된 예약의 id값이 필요합니다.")
    @PatchMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage> editReservation(
            @Parameter(description = "예약 id 값", required = true, example = "3")
            @PathVariable Long reservationId,
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 수정 성공", reservationService.editReservation(reservationId, requestDto, userDetails));
    }

    /**
     * 예약 삭제
     */
    @Operation(summary = "예약 삭제",
            description = "회원 자신이 등록한 예약을 삭제합니다. DB에 등록된 예약의 id값이 필요합니다.")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage> deleteReservation(
            @Parameter(description = "예약 id 값", required = true, example = "3")
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 삭제 성공", reservationService.deleteReservation(reservationId, userDetails));
    }
}
