package com.example.chillisauce.schedules.exception;

import com.example.chillisauce.message.ErrorStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ScheduleErrorCode implements ErrorStatusMessage {
    /* 400 */
    NOT_PROPER_TIME(BAD_REQUEST, "유효한 시간 범위가 아닙니다."),

    /* 404 */
    SCHEDULE_ROOM_NOT_FOUND(NOT_FOUND, "스케줄을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
