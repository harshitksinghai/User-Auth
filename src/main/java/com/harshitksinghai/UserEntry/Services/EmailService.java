package com.harshitksinghai.UserEntry.Services;

public interface EmailService {

    void sendOTPLinkEmail(String email, String otp, String link);

    void sendResetPasswordLink(String email, String link);
}
