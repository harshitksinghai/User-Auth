package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import org.springframework.transaction.annotation.Transactional;


public interface OTPService {

    String generateOTP();

    @Transactional
    void addOTPDetails(String email, String otp);

    @Transactional
    boolean verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);

    @Transactional
    void clearExpiredOTPs();
}
