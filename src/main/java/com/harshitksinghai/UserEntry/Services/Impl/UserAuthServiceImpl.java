package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserLogoutRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.RefreshTokenRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLogoutResponseDTO;
import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    private final Logger LOG = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    OTPService otpService;

    @Autowired
    LinkService linkService;

    @Autowired
    JwtUtils jwtUtils;
    @Override
    public ResponseEntity<String> verifyEmail(VerifyEmailRequestDTO verifyEmailRequestDTO) {
        LOG.info("inside verifyEmail in UserAuthServiceImpl");
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
        LOG.info("inside emailFieldEditedAction in UserAuthServiceImpl");
        return null;
    }

    public UserLoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {
        LOG.info("inside refreshToken in UserAuthServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshTokenRequestDTO.getRefreshToken());

        if (refreshTokenOpt.isEmpty()) {
            LOG.info("Refresh token is not in database!");
            userLoginResponseDTO.setMessage("Refresh token is not in database!");
            userLoginResponseDTO.setSuccess(false);
            return userLoginResponseDTO;
        }

        RefreshToken refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
        String email = refreshToken.getEmail();

        String jwtToken = jwtUtils.generateToken(email);

        userLoginResponseDTO.setJwtToken(jwtToken);
        userLoginResponseDTO.setRefreshToken(refreshTokenRequestDTO.getRefreshToken());
        userLoginResponseDTO.setSuccess(true);
        userLoginResponseDTO.setMessage("Refresh token function executed successfully");

        return userLoginResponseDTO;
    }

    @Override
    public ResponseEntity<String> clearExpiredOTPsLinks() {
        LOG.info("inside clearExpiredOTPsLinks in UserAuthServiceImpl");

        LOG.info("clearing expired otps");
        otpService.clearExpiredOTPs();
        LOG.info("expired otps cleared successfully");

        LOG.info("clearing expired links");
        linkService.clearExpiredLinks();
        LOG.info("expired links cleared successfully");

        return new ResponseEntity<>("successfully cleared expired otps and links", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserLogoutResponseDTO> logoutUser(UserLogoutRequestDTO userLogoutRequestDTO) {
        UserLogoutResponseDTO userLogoutResponseDTO = new UserLogoutResponseDTO();

        String jwtToken = userLogoutRequestDTO.getJwtToken();
        jwtUtils.invalidateToken(jwtToken);

        if(userLogoutRequestDTO.getRefreshToken() == null){
            userLogoutResponseDTO.setMessage("invalid request credentials, refresh token not provided");
            userLogoutResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLogoutResponseDTO, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOpt = userService.findByEmail(userLogoutRequestDTO.getEmail());
        if(userOpt.isEmpty()){
            userLogoutResponseDTO.setMessage("user with provided email does not exist");
            userLogoutResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLogoutResponseDTO, HttpStatus.BAD_REQUEST);
        }
        userOpt.get().setRefreshToken(null);
        userService.saveUserDetails(userOpt.get());
        refreshTokenService.deleteByToken(userLogoutRequestDTO.getRefreshToken());

        userLogoutResponseDTO.setMessage("user logged out successfully");
        userLogoutResponseDTO.setSuccess(true);
        return new ResponseEntity<>(userLogoutResponseDTO, HttpStatus.OK);
    }
}
