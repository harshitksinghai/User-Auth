package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.RefreshTokenRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.RefreshTokenService;
import com.harshitksinghai.UserEntry.Services.UserAuthService;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;
    @Override
    public ResponseEntity<String> verifyEmail(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        Optional<User> userOpt = userService.findByEmail(verifyEmailRequestDTO.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (!user.getIsVerified()) {
                return new ResponseEntity<>("email not verified", HttpStatus.FORBIDDEN);
            }

            if (user.getUsername() == null) {
                return new ResponseEntity<>("no username -> show login code field -> onboarding", HttpStatus.OK);
            } else if (user.getPassword() == null) {
                return new ResponseEntity<>("yes username, no password -> show login code field -> main", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("yes username, yes password -> show type password field and forgot password option -> main", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("no email -> show verification code field -> onboarding", HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<String> emailFieldEditedAction() {
        return null;
    }

    public UserLoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshTokenRequestDTO.getRefreshToken());

        if (refreshTokenOpt.isEmpty()) {
            throw new RuntimeException("Refresh token is not in database!");
        }

        RefreshToken refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
        String email = refreshToken.getEmail();

        String accessToken = jwtUtils.generateToken(email);

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        userLoginResponseDTO.setJwtToken(accessToken);
        userLoginResponseDTO.setRefreshToken(refreshTokenRequestDTO.getRefreshToken());
        userLoginResponseDTO.setSuccess(true);
        userLoginResponseDTO.setMessage("Refresh token function executed successfully");

        return userLoginResponseDTO;
    }

}
