package com.example.chillisauce.schedules.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleException extends RuntimeException{
    private final ScheduleErrorCode errorCode;
}
