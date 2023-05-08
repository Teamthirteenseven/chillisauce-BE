package com.example.chillisauce.reservations.controller;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.dto.request.ReservationRequest;
import com.example.chillisauce.reservations.dto.response.ReservationListResponse;
import com.example.chillisauce.reservations.dto.response.ReservationResponse;
import com.example.chillisauce.reservations.dto.response.ReservationTimetableResponse;
import com.example.chillisauce.reservations.service.ReservationService;
import com.example.chillisauce.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.print.Pageable;
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
    public ResponseEntity<ResponseMessage<ReservationListResponse>> getAllReservations(
            @Parameter(description = "회사 이름", required = true, example = "testCompany")
            @PathVariable String companyName,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("전체 예약 조회 성공", reservationService.getAllReservations(companyName, page-1, userDetails));
    }

    /**
     * 1개 회의실의 해당 날짜의 예약 타임 테이블 조회
     * 쿼리파라미터가 없으면 오늘 날짜의 예약 타임 테이블 조회
     */
    @Operation(summary = "예약 타임테이블 조회",
            description = "특정 회의실의 특정 날짜 예약 내역을 타임단위로 조회합니다.")
    @GetMapping("/reservations/{meetingRoomId}")
    public ResponseEntity<ResponseMessage<ReservationTimetableResponse>> getReservationTimetable(
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
    public ResponseEntity<ResponseMessage<ReservationResponse>> addReservation(
            @Parameter(description = "회의실 id 값", required = true, example = "3")
            @PathVariable Long meetingRoomId,
            @RequestBody @Valid ReservationRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 등록 성공", reservationService.addReservation(meetingRoomId, request, userDetails));
    }

    /**
     * 예약 수정
     */
    @Operation(summary = "예약 수정",
            description = "회원 자신이 등록한 예약을 수정합니다. DB에 등록된 예약의 id값이 필요합니다.")
    @PatchMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage<ReservationResponse>> editReservation(
            @Parameter(description = "예약 id 값", required = true, example = "3")
            @PathVariable Long reservationId,
            @RequestBody @Valid ReservationRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 수정 성공", reservationService.editReservation(reservationId, request, userDetails));
    }

    /**
     * 예약 삭제
     */
    @Operation(summary = "예약 삭제",
            description = "회원 자신이 등록한 예약을 삭제합니다. DB에 등록된 예약의 id값이 필요합니다.")
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ResponseMessage<String>> deleteReservation(
            @Parameter(description = "예약 id 값", required = true, example = "3")
            @PathVariable Long reservationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage
                .responseSuccess("예약 삭제 성공", reservationService.deleteReservation(reservationId, userDetails));
    }

    @DeleteMapping("/reservations/meetingRoom/{meetingRoomId}")
    public ResponseEntity<ResponseMessage<String>> deleteMeetingRoomInReservations(
            @Parameter(description = "회의실 id 값", required = true, example = "1")
            @PathVariable Long meetingRoomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseMessage.responseSuccess("회의실 전체 예약 삭제 성공",
                reservationService.deleteMeetingRoomInReservations(meetingRoomId, userDetails));
    }
}
