package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserLoginService{
    ResponseEntity<UserLoginResponseDTO> verifyPassword(UserLoginRequestDTO userLoginRequestDTO);

    ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    ResponseEntity<UserLoginResponseDTO> verifyLoginResetPasswordLink(String code);

    ResponseEntity<UserLoginResponseDTO> verifyTempLoginLink(String code);

    ResponseEntity<String> sendLoginCode(UserTempLoginRequestDTO userTempLoginRequestDTO);

    ResponseEntity<UserLoginResponseDTO> verifyTempLoginOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);
}
