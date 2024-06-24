package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
import org.springframework.http.ResponseEntity;

public interface UserLoginService{
    ResponseEntity<String> verifyPassword(UserLoginRequestDTO userLoginRequestDTO);

    ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    ResponseEntity<String> verifyLoginResetPasswordLink(String code);

    ResponseEntity<String> verifyTempLoginLink(String code);

    ResponseEntity<String> sendLoginCode(UserTempLoginRequestDTO userTempLoginRequestDTO);

    ResponseEntity<String> verifyTempLoginOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);
}
