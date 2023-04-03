package com.example.chillisauce.reservations.exception;

import com.example.chillisauce.message.ErrorStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements ErrorStatusMessage {
    /* 400 */
    NOT_PROPER_TIME(BAD_REQUEST, "유효한 시간이 아닙니다."),
    /* 404 */
    MEETING_ROOM_NOT_FOUND(NOT_FOUND, "등록된 회의실이 없습니다.");

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