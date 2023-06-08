package com.example.chillisauce.global.message;

import org.springframework.http.HttpStatus;

public interface ErrorStatusMessage {
    HttpStatus getHttpStatus();
    String getMessage();
}
