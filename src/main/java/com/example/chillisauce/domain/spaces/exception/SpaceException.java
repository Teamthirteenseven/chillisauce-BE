package com.example.chillisauce.domain.spaces.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpaceException extends RuntimeException {
    private final SpaceErrorCode errorCode;

}
