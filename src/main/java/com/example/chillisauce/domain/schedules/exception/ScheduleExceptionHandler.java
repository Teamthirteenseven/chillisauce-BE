package com.example.chillisauce.domain.schedules.exception;

import com.example.chillisauce.global.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ScheduleExceptionHandler {
    @ExceptionHandler(value = { ScheduleException.class })
    protected ResponseEntity<ResponseMessage<Object>> handleReservationException(ScheduleException e) {
        return ResponseMessage.responseError(e.getMessage(), e.getStatusCode());
    }
}
