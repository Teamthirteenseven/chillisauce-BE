package com.example.chillisauce.reservations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 예약 정보 타임테이블
 */
@Getter
@AllArgsConstructor
@Schema(description = "예약 타임테이블 응답 DTO")
public class ReservationTimetableResponseDto {
    @Schema(description = "회의실 Id")
    Long mrId;

    @Schema(description = "타임테이블 리스트")
    List<ReservationTimeResponseDto> timeList;
}
