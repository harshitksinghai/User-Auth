package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserOnBoardRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserSignUpRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserRegisterService {

    ResponseEntity<String> signUpUser(UserSignUpRequestDTO userSignUpRequestDTO);

    ResponseEntity<String> onBoardUser(UserOnBoardRequestDTO userOnBoardRequestDTO);

    ResponseEntity<UserLoginResponseDTO> verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO);

    ResponseEntity<UserLoginResponseDTO> verifyLink(String code);
}
