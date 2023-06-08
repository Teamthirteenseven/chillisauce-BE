package com.example.chillisauce.domain.spaces.exception;

import com.example.chillisauce.global.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SpaceExceptionHandler {
    @ExceptionHandler(value = { SpaceException.class })
    protected ResponseEntity<ResponseMessage<Object>> handleCustomException(SpaceException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ResponseMessage.responseError(e.getErrorCode());
    }
}