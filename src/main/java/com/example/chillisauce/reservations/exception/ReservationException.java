package com.example.chillisauce.reservations.exception;

import com.example.chillisauce.schedules.exception.ScheduleErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationException extends RuntimeException {
    private final String message;
    private final HttpStatus statusCode;
    public ReservationException(ReservationErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.statusCode = errorCode.getHttpStatus();
    }
}
