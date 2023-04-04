package com.example.chillisauce.reservations.dto;

import com.example.chillisauce.reservations.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 예약 타임테이블의 1개 요소 : 1시간 단위 start - end
 */
@Slf4j
@Getter
public class ReservationTimeResponseDto {
    Boolean isCheckOut;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    LocalTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    LocalTime end;

    public ReservationTimeResponseDto(Boolean isCheckOut, LocalTime start, LocalTime end) {
        this.isCheckOut = isCheckOut;
        this.start = LocalTime.of(start.getHour(), start.getMinute());
        this.end = LocalTime.of(end.getHour(), end.getMinute());
    }
}
