package com.example.chillisauce.reservations.exception;

import com.example.chillisauce.message.ErrorStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements ErrorStatusMessage {
    /* 400 */
    NOT_PROPER_TIME(BAD_REQUEST, "유효한 시간 범위가 아닙니다."),
    DUPLICATED_TIME(BAD_REQUEST, "해당 시간대에 이미 등록된 예약이 있습니다."),
    INVALID_USER_RESERVATION_UPDATE(BAD_REQUEST, "예약을 수정할 권한이 없는 유저입니다"),

    /* 404 */
    MEETING_ROOM_NOT_FOUND(NOT_FOUND, "등록된 회의실이 없습니다."),
    RESERVATION_NOT_FOUND(NOT_FOUND, "예약을 찾을 수 없습니다."),
    COMPANY_NOT_FOUND(NOT_FOUND, "회사를 찾을 수 없습니다");

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
