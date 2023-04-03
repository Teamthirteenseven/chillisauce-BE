package com.example.chillisauce.users.exception;

import com.example.chillisauce.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(value = { UserException.class })
    protected ResponseEntity<ResponseMessage> handleCustomException(UserException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ResponseMessage.responseError(e.getErrorCode());

    }
}