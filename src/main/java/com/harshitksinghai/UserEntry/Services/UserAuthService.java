package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import org.springframework.http.ResponseEntity;

public interface UserAuthService {
    ResponseEntity<String> verifyEmail(VerifyEmailRequestDTO verifyEmailRequestDTO);

    ResponseEntity<String> emailFieldEditedAction();
}
