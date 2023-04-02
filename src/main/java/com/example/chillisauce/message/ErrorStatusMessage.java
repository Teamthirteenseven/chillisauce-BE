package com.example.chillisauce.message;

import org.springframework.http.HttpStatus;

public interface ErrorStatusMessage {
    HttpStatus getHttpStatus();
    String getMessage();
}
