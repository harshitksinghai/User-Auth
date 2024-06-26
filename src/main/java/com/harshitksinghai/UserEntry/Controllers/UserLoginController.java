package com.harshitksinghai.UserEntry.Controllers;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Services.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/login")
public class UserLoginController {

    @Autowired
    UserLoginService userLoginService;

    @PostMapping("/verify-password")
    public ResponseEntity<UserLoginResponseDTO> verifyPassword(@RequestBody UserLoginRequestDTO userLoginRequestDTO){
        return userLoginService.verifyPassword(userLoginRequestDTO);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO){
        return userLoginService.forgotPassword(forgotPasswordRequestDTO);
    }

    @GetMapping("/verify-login-reset-password-link")
    public ResponseEntity<UserLoginResponseDTO> verifyLoginResetPasswordLink(@RequestParam("code") String code){
        return userLoginService.verifyLoginResetPasswordLink(code);
    } // when link is verified redirect to main/reset-password endpoint

    @PostMapping("/login-code")
    public ResponseEntity<String> sendLoginCode(@RequestBody UserTempLoginRequestDTO userTempLoginRequestDTO){
        return userLoginService.sendLoginCode(userTempLoginRequestDTO);
    }

    @GetMapping("/verify-temp-login-link")
    public ResponseEntity<UserLoginResponseDTO> verifyTempLoginLink(@RequestParam("code") String code){
        return userLoginService.verifyTempLoginLink(code);
    }

    @PostMapping("/verify-temp-login-otp")
    public ResponseEntity<UserLoginResponseDTO> verifyTempLoginOTP(@RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO){
        return userLoginService.verifyTempLoginOTP(verifyOTPRequestDTO);
    }
}
