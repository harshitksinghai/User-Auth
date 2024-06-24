package com.harshitksinghai.UserEntry.Controllers;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserOnBoardRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserSignUpRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import com.harshitksinghai.UserEntry.Services.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
public class UserRegisterController {

    @Autowired
    UserRegisterService userRegisterService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpUser(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO){
        return userRegisterService.signUpUser(userSignUpRequestDTO);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestBody VerifyOTPRequestDTO verifyOTPRequestDTO){
        return userRegisterService.verifyOTP(verifyOTPRequestDTO);
    }

    @PostMapping("/onboarding")
    public ResponseEntity<String> onBoardUser(@RequestBody UserOnBoardRequestDTO userOnBoardRequestDTO){
        return userRegisterService.onBoardUser(userOnBoardRequestDTO);
    }

    @PostMapping("/verify-link")
    public ResponseEntity<String> verifyLink(@RequestParam("code") String code){
        return userRegisterService.verifyLink(code);
    }

}
