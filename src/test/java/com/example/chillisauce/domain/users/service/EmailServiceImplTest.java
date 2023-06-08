package com.example.chillisauce.domain.users.service;

import com.example.chillisauce.domain.users.exception.UserException;
import com.example.chillisauce.domain.users.service.EmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;

    @Nested
    @DisplayName("성공 케이스")
    class SuccessCase {

        @DisplayName("메일 발송 성공")
        @Test
        void mailSend() throws Exception {
            //given
            String to = "123@123";

            Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            //when
            emailService.sendSimpleMessage(to);

            //then
            verify(mailSender, times(1)).createMimeMessage();

        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failCase {

        @DisplayName("메일 발송 실패 (이메일 형식 오류)")
        @Test
        void fail1() {
            //given
            String to = "1234";

            //when
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doThrow(MailSendException.class).when(mailSender).send(any(MimeMessage.class));

            UserException exception = assertThrows(UserException.class, () -> {
                emailService.sendSimpleMessage(to);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("이메일 형식이 맞지 않습니다.");
        }

        @DisplayName("메일 발송 실패 (1분당 2회요청 초과)")
        @Test
        void fail2() {
            //given
            String to = "1234@1234";

            //when
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doNothing().when(mailSender).send(any(MimeMessage.class));

            //2번의 요청을 진행
            assertDoesNotThrow(() -> {
                emailService.sendSimpleMessage(to);
            });
            assertDoesNotThrow(() -> {
                emailService.sendSimpleMessage(to);
            });

            UserException exception = assertThrows(UserException.class, () -> {
                emailService.sendSimpleMessage(to);
            });

            //then
            assertThat(exception).isNotNull();
            assertThat(exception.getErrorCode().getMessage()).isEqualTo("반복된 요청입니다. 잠시 후 사용해주세요.");
        }
    }
}