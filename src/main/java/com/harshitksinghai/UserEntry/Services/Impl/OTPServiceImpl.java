package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import com.harshitksinghai.UserEntry.Models.OTPVerification;
import com.harshitksinghai.UserEntry.Repositories.OTPVerificationRepository;
import com.harshitksinghai.UserEntry.Services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {

    @Autowired
    OTPVerificationRepository otpVerificationRepository;
    @Override
    public String generateOTP() {
        // Define the length of the OTP
        final int OTP_LENGTH = 6; // 6 characters long OTP

        // OTP characters allowed
        final String OTP_CHARACTERS = "0123456789";

        // Random generator
        final Random random = new SecureRandom();

        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(OTP_CHARACTERS.charAt(random.nextInt(OTP_CHARACTERS.length())));
        }
        return otp.toString();
    }

    @Override
    @Transactional
    public void addOTPDetails(String email, String otp) {
        OTPVerification otpVerification = new OTPVerification();
        otpVerification.setOtp(otp);
        otpVerification.setEmail(email);
        otpVerification.setExpirationTime(LocalDateTime.now().plusMinutes(1).plusSeconds(30));

        otpVerificationRepository.save(otpVerification);
    }

    @Override
    @Transactional
    public boolean verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        String otp = verifyOTPRequestDTO.getOtp();
        Optional<OTPVerification> otpVerification = otpVerificationRepository.findByOtp(otp);
        System.out.println("im here 1");
        if(!otpVerification.isPresent()){
            System.out.println("im here 2");

            return false;
        }
        if(otpVerification.get().getExpirationTime().isAfter(LocalDateTime.now())){
            System.out.println("im here 3");

            otpVerificationRepository.deleteByOtp(otp);
            System.out.println("im here 4");

            return true;
        }
        System.out.println("im here 5");

        otpVerificationRepository.deleteByOtp(otp);
        System.out.println("im here 6");

        return false;
    }

}
