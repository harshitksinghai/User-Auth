package com.harshitksinghai.UserEntry.Controllers;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.Services.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    UserAuthService userAuthService;

    @PostMapping("/")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO){
        return userAuthService.verifyEmail(verifyEmailRequestDTO);
    }

    @PostMapping("/email-edited")
    public ResponseEntity<String> emailFieldEditedAction(){
        return userAuthService.emailFieldEditedAction();
    }
} // not sure if needed, so not yet implemented

