package com.bookstore.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送服务
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送邮箱验证码
     */
    public void sendVerifyCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1940794315@qq.com");
        message.setTo(toEmail);
        message.setSubject("在线图书商城 - 邮箱验证码");
        message.setText("您的验证码是：" + code + "，有效期 5 分钟，请勿泄露。");
        mailSender.send(message);
    }
}
