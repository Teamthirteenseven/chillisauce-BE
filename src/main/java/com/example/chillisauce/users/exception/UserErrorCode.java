package com.example.chillisauce.users.exception;

import com.example.chillisauce.message.ErrorStatusMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorStatusMessage {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_TOKEN(UNAUTHORIZED, "토큰이 유효하지 않습니다"),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다"),
    INVALID_CERTIFICATION(UNAUTHORIZED, "인증번호가 유효하지 않습니다"),
    DUPLICATE_EMAIL(BAD_REQUEST, "중복된 이메일이 존재합니다"),
    DUPLICATE_COMPANY(BAD_REQUEST, "중복된 회사명이 존재합니다"),
    NOT_PROPER_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_PROPER_CERTIFICATION(BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    NOT_PROPER_EMAIL(BAD_REQUEST, "이메일 형식이 맞지 않습니다."),
    NOT_HAVE_PERMISSION(BAD_REQUEST, "권한이 없습니다."),
    NOT_AUTHOR(BAD_REQUEST, "작성자만 삭제/수정할 수 있습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "등록된 사용자가 없습니다"),
    COMPANY_NOT_FOUND(NOT_FOUND, "등록된 회사가 없습니다"),
    ;
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