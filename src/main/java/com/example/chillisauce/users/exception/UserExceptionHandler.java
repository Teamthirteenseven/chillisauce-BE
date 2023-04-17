package com.example.chillisauce.users.exception;

import com.example.chillisauce.message.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(value = { UserException.class })
    protected ResponseEntity<ResponseMessage> handleCustomException(UserException e) {
        return ResponseMessage.responseError(e.getMessage(), e.getStatusCode());

    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ResponseMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getFieldError() == null ? "" : e.getFieldError().getDefaultMessage();
        return ResponseMessage.responseError(message, HttpStatus.BAD_REQUEST);
    }
}