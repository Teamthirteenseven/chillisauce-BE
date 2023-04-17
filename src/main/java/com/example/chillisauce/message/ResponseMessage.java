package com.example.chillisauce.message;

import com.example.chillisauce.spaces.exception.SpaceErrorCode;
import com.example.chillisauce.users.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ResponseMessage {
    private final String message;
    private final int statusCode;
    private final Object data;


    public static ResponseEntity<ResponseMessage> responseError(ErrorStatusMessage errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseMessage.builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .data("")
                        .build()
                );
    }

    public static ResponseEntity<ResponseMessage> responseError(String message, HttpStatus statusCode) {
        return ResponseEntity
                .status(statusCode)
                .body(ResponseMessage.builder()
                        .statusCode(statusCode.value())
                        .message(message)
                        .data("")
                        .build()
                );
    }

    public static ResponseEntity<ResponseMessage> responseSuccess(String message, Object data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseMessage.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
                );
    }
}