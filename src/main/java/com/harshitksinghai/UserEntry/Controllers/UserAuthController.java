package com.harshitksinghai.UserEntry.Controllers;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserLogoutRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.RefreshTokenRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLogoutResponseDTO;
import com.harshitksinghai.UserEntry.Services.RefreshTokenService;
import com.harshitksinghai.UserEntry.Services.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    UserAuthService userAuthService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO){
        return userAuthService.verifyEmail(verifyEmailRequestDTO);
    }

    @PostMapping("/refresh-token")
    public UserLoginResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return userAuthService.refreshToken(refreshTokenRequestDTO);
    }

    @GetMapping("/clear-expired-otps-links")
    public ResponseEntity<String> clearExpiredOTPLink(){
        return userAuthService.clearExpiredOTPsLinks();
    }

    @PostMapping("/logout")
    public ResponseEntity<UserLogoutResponseDTO> logoutUser(@RequestBody UserLogoutRequestDTO userLogoutRequestDTO){
        return userAuthService.logoutUser(userLogoutRequestDTO);
    }

    @PostMapping("/email-edited")
    public ResponseEntity<String> emailFieldEditedAction(){
        return userAuthService.emailFieldEditedAction();
    }
} // not sure if needed, so not yet implemented

