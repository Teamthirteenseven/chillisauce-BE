package com.example.chillisauce.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

@Builder
public record ResponseMessage<T>(String message, int statusCode, T data) {
    public static ResponseEntity<ResponseMessage<Object>> responseError(ErrorStatusMessage errorCode) {

        return ResponseEntity
                .status(errorCode.getHttpStatus())

                .body(ResponseMessage.<Object>builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .data("")
                        .build()
                );
    }

    public static ResponseEntity<ResponseMessage<Object>> responseError(String message, HttpStatus statusCode) {
        return ResponseEntity
                .status(statusCode)
                .body(ResponseMessage.<Object>builder()
                        .statusCode(statusCode.value())
                        .message(message)
                        .data("")
                        .build()
                );
    }

    public static <T> ResponseEntity<ResponseMessage<T>> responseSuccess(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseMessage.<T>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
                );
    }
}