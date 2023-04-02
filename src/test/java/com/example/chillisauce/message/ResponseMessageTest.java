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
    public void 메시지_인터페이스를_파라미터로_받으면_에러를_응답() {
        // given
        ErrorStatusMessage errorStatusMessage = new ErrorStatusMessageImpl(HttpStatus.BAD_REQUEST, "error message");

        // when
        ResponseEntity<ResponseMessage> responseMessageResponseEntity = ResponseMessage.responseError(errorStatusMessage);

        // then
        assertThat(responseMessageResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseMessageResponseEntity.getBody()).isNotNull();
        assertThat(responseMessageResponseEntity.getBody().getMessage()).isEqualTo("error message");
    }
}