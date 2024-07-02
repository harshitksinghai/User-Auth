package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserLogoutRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.RefreshTokenRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLogoutResponseDTO;
import org.springframework.http.ResponseEntity;

public interface UserAuthService {
    ResponseEntity<String> verifyEmail(VerifyEmailRequestDTO verifyEmailRequestDTO);

    ResponseEntity<String> emailFieldEditedAction();

    UserLoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO);

    ResponseEntity<String> clearExpiredOTPsLinks();

    ResponseEntity<UserLogoutResponseDTO> logoutUser(UserLogoutRequestDTO userLogoutRequestDTO);
}
