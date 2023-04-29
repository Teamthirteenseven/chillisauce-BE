package com.example.chillisauce.users.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class UserException extends RuntimeException {
    private final UserErrorCode errorCode;
}
