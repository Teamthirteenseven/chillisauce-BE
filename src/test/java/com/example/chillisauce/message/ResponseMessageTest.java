package com.example.chillisauce.message;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMessageTest {
    // ErrorStatusMessage 인터페이스 구현 클래스
    static class ErrorStatusMessageImpl implements ErrorStatusMessage {
        HttpStatus httpStatus;
        String message;

        public ErrorStatusMessageImpl(HttpStatus httpStatus, String message) {
            this.httpStatus = httpStatus;
            this.message = message;
        }

        @Override
        public HttpStatus getHttpStatus() {
            return this.httpStatus;
        }

        @Override
        public String getMessage() {
            return this.message;
        }
    }

    @Test
    public void 에러응답테스트_에러상태메시지_인터페이스를_파라미터로_받기() {
        // given
        ErrorStatusMessage errorStatusMessage = new ErrorStatusMessageImpl(HttpStatus.BAD_REQUEST, "error message");

        // when
        ResponseEntity<ResponseMessage<Object>> result = ResponseMessage.responseError(errorStatusMessage);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo("error message");
    }

    @Test
    public void 성공응답테스트() {
        // given
        String message = "응답 성공";
        Object data = new Object();

        // when
        ResponseEntity<ResponseMessage<Object>> result = ResponseMessage.responseSuccess(message, data);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().message()).isEqualTo("응답 성공");
    }
}