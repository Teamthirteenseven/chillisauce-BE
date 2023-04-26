package com.example.chillisauce.schedules.exception;

import com.example.chillisauce.message.ResponseMessage;
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
