package com.example.chillisauce.reservations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 예약 정보 타임테이블
 */
@Getter
@AllArgsConstructor
public class ReservationTimetableResponseDto {
    Long mrId;
    List<ReservationTimeResponseDto> timeList;
}
