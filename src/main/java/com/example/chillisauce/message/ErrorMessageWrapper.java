package com.example.chillisauce.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ErrorMessageWrapper {
    String message;
    HttpStatus statusCode;
}
