package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserOnBoardRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserSignUpRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UserRegisterService {

    ResponseEntity<String> signUpUser(UserSignUpRequestDTO userSignUpRequestDTO);

    ResponseEntity<String> onBoardUser(UserOnBoardRequestDTO userOnBoardRequestDTO);

    ResponseEntity<String> verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);

    ResponseEntity<String> verifyLink(String code);
}
