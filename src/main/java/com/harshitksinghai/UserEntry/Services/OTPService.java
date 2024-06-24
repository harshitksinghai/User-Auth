package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;


public interface OTPService {

    String generateOTP();

    void addOTPDetails(String email, String otp);

    boolean verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);

}
