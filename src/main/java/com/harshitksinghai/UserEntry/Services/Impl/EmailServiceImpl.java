package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("$(spring.mail.username)")
    private String fromMail;
    @Override
    public void sendOTPLinkEmail(String email, String otp, String link) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject("Your OTP and Magic Link");
        simpleMailMessage.setText("Your OTP is: " + otp + "\nUse the following link to complete your signup: " + link);
        simpleMailMessage.setTo(email);

        mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendResetPasswordLink(String email, String link) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject("Your Reset Password Link");
        simpleMailMessage.setText("Here is your reset password link:\n" + link);
        simpleMailMessage.setTo(email);

        mailSender.send(simpleMailMessage);
    }
}
