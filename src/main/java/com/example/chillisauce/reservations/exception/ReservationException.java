package com.example.chillisauce.reservations.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationException extends RuntimeException {
    private final ReservationErrorCode errorCode;
}
