package com.example.chillisauce.domain.schedules.exception;

import com.example.chillisauce.global.message.ErrorStatusMessage;
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
    DUPLICATED_TIME(BAD_REQUEST, "해당 시간대에 이미 등록된 스케줄이 있습니다."),
    INVALID_USER_SCHEDULE_UPDATE(BAD_REQUEST, "스케줄을 수정할 권한이 없는 유저입니다."),
    INVALID_USER_SCHEDULE_DELETE(BAD_REQUEST, "스케줄을 삭제할 권한이 없는 유저입니다."),
    /* 404 */
    SCHEDULE_NOT_FOUND(NOT_FOUND, "스케줄을 찾을 수 없습니다.");


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
