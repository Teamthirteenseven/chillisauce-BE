package com.example.chillisauce.message;

import com.example.chillisauce.users.exception.UserErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor
public class ResponseMessage {
    private final String message;
    private final int statusCode;
    private final Object data;

    public static ResponseEntity ErrorResponse(UserErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseMessage.builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .message(errorCode.getMessage())
                        .data("")
                        .build()
                );
    }

    //fixme : reservationsErrorCode 만들고 그걸로 수정.
//    public static ResponseEntity ErrorResponse(ReservationErrorCode errorCode) {
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(ResponseMessage.builder()
//                        .statusCode(errorCode.getHttpStatus().value())
//                        .message(errorCode.getMessage())
//                        .data("")
//                        .build()
//                );
//    }

    //fixme : spaceErrorCode 만들고 그걸로 수정.
//    public static ResponseEntity ErrorResponse(ProductErrorCode errorCode) {
//        return ResponseEntity
//                .status(errorCode.getHttpStatus())
//                .body(ResponseMessage.builder()
//                        .statusCode(errorCode.getHttpStatus().value())
//                        .message(errorCode.getMessage())
//                        .data("")
//                        .build()
//                );
//    }

    public static ResponseEntity SuccessResponse(String message, Object data) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseMessage.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(message)
                        .data(data)
                        .build()
                );
    }
}