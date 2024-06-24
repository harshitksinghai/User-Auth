package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyEmailRequestDTO;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.UserAuthService;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    UserService userService;
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
}
