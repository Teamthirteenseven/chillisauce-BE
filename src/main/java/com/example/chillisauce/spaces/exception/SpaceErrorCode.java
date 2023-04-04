package com.example.chillisauce.spaces.exception;

import com.example.chillisauce.message.ErrorStatusMessage;
import com.example.chillisauce.message.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum SpaceErrorCode implements ErrorStatusMessage {
    /* 400 BAD_REQUEST : 잘못된 요청 */

    NOT_HAVE_PERMISSION(BAD_REQUEST, "권한이 없습니다."),

    //    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */

    SPACE_NOT_FOUND(NOT_FOUND, "해당 공간을 찾을 수 없습니다."),

    BOX_NOT_FOUND(NOT_FOUND, "해당 자리를 찾을 수 없습니다."),
    MR_NOT_FOUND(NOT_FOUND, "해당 회의실을 찾을 수 없습니다."),

    COMPANIES_NOT_FOUND(NOT_FOUND,"해당 회사가 존재하지 않습니다."),


    ;
    private final HttpStatus httpStatus;
    private final String message;
}