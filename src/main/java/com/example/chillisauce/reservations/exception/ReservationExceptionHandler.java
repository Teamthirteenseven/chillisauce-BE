package com.example.chillisauce.reservations.exception;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.reservations.controller.ReservationController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = ReservationController.class)
public class ReservationExceptionHandler {
    @ExceptionHandler(value = {ReservationException.class})
    protected ResponseEntity<ResponseMessage> handleReservationException(ReservationException e) {
        return ResponseMessage.responseError(e.getMessage(), e.getStatusCode());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ResponseMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getFieldError() == null ? "" : e.getFieldError().getDefaultMessage();
        return ResponseMessage.responseError(message, HttpStatus.BAD_REQUEST);
    }
}
