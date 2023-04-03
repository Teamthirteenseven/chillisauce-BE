package com.example.chillisauce.reservations.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationException extends RuntimeException {
    private final ReservationErrorCode errorCode;
}
