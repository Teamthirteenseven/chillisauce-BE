package com.example.chillisauce.domain.reservations.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
