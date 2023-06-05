package com.example.chillisauce.users.service;

import com.example.chillisauce.users.exception.UserErrorCode;
import com.example.chillisauce.users.exception.UserException;
import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;
    public static final String certification = createKey();

    /* 1분당 2회 이메일 인증 요청 제한 */
    RequestLimitRule limit = RequestLimitRule.of(Duration.ofMinutes(1), 2);
    RequestRateLimiter rateLimiter = new InMemorySlidingWindowRequestRateLimiter(limit);

    private MimeMessage createMessage(String to, String certificationKey) throws Exception {

        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("Flexidesk 인증코드 발송");//제목

        StringBuffer msgg = new StringBuffer();
        msgg.append("<div style='margin:20px;'>");
        msgg.append("<h1> 안녕하세요 Flexidesk 입니다. </h1>");
        msgg.append("<br>");
        msgg.append("<p>아래 코드를 입력해주세요<p>");
        msgg.append("<br>");
        msgg.append("<p>감사합니다.<p>");
        msgg.append("<br>");
        msgg.append("<div align='center' style='border:1px solid black; font-family:verdana';>");
        msgg.append("<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>");
        msgg.append("<div style='font-size:130%'>");
        msgg.append("CODE : <strong>");
        msgg.append(certificationKey).append("</strong><div><br/> ");
        msgg.append("</div>");
        message.setText(String.valueOf(msgg), "utf-8", "html");//내용
        message.setFrom(new InternetAddress("limsanggyu91@gmail.com", "Flexidesk"));//보내는 사람

        return message;
    }

    /* 8자리 랜덤 난수 생성 */
    public static String createKey() {
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) {
            int index = rnd.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    @Override
    public String sendSimpleMessage(String to) throws Exception {

        if (rateLimiter.overLimitWhenIncremented(to)) {
            throw new UserException(UserErrorCode.USAGE_LIMIT);
        }

        String certificationKey = createKey();
        MimeMessage message = createMessage(to, certificationKey);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new UserException(UserErrorCode.NOT_PROPER_EMAIL);
        }
        return "certification : " + certificationKey;
    }



}
