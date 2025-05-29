package com.dita.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SecureRandom random = new SecureRandom();

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** 6자리 인증코드 생성 */
    public String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /** 이메일로 인증번호 발송 */
    public void sendVerificationCode(String toEmail, String code) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
        helper.setTo(toEmail);
        helper.setFrom("netflix720222@gmail.com");
        helper.setSubject("메디링크 이메일 인증번호");
        helper.setText(
            "<p>안녕하세요. 메디링크입니다.</p>" +
            "<p>회원가입 인증번호는 <b>" + code + "</b> 입니다.</p>" +
            "<p>타인에게 알려주거나 보여주시면 안됩니다.</p>",
            true
        );
        mailSender.send(msg);
    }
}
