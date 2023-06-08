package com.example.chillisauce.domain.reservations.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

/**
 * 예약 시 시간 단위
 */
@Getter
@AllArgsConstructor
public class TimeUnit {
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime start;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime end;
}
