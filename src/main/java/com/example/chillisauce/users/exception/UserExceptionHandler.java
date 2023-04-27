package com.example.chillisauce.users.exception;

import com.example.chillisauce.message.ResponseMessage;
import com.example.chillisauce.spaces.exception.SpaceException;
import com.example.chillisauce.users.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class UserExceptionHandler {
//    @ExceptionHandler(value = { UserException.class })
//    protected ResponseEntity<ResponseMessage<Object>> handleCustomException(UserException e) {
//        return ResponseMessage.responseError(e.getErrorCode());
//
//    }

    /* 테스트*/

    private final ObjectMapper objectMapper;
    @ExceptionHandler(value = { UserException.class })
    protected ResponseEntity<ResponseMessage<Object>> handleCustomException(UserException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        ResponseEntity<ResponseMessage<Object>> responseEntity = ResponseMessage.responseError(e.getErrorCode());
        String bodyStr = null;
        try {
            bodyStr = objectMapper.writeValueAsString(responseEntity.getBody());
        } catch (JsonProcessingException ex) {
            log.error("Failed to convert response body to JSON string", ex);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(Objects.requireNonNull(bodyStr).getBytes(StandardCharsets.UTF_8).length);
        Charset utf8 = StandardCharsets.UTF_8;
        MediaType mediaType = new MediaType("application", "json", utf8);
        headers.setContentType(mediaType);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(responseEntity.getBody(), headers, responseEntity.getStatusCode());
    }
    /* 테스트*/
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    protected ResponseEntity<ResponseMessage<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getFieldError() == null ? "" : e.getFieldError().getDefaultMessage();
        return ResponseMessage.responseError(message, HttpStatus.BAD_REQUEST);
    }
}