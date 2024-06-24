package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    UserService userService;

    @Autowired
    LinkService linkService;

    @Autowired
    EmailService emailService;

    @Autowired
    OTPService otpService;

    @Value("${site.url}")
    private String siteURL;

    @Override
    public ResponseEntity<String> verifyPassword(UserLoginRequestDTO userLoginRequestDTO) {
        Optional<User> userOpt = userService.findByEmail(userLoginRequestDTO.getEmail());
        if(userOpt.isPresent()){
            if(userOpt.get().getPassword().equals(userLoginRequestDTO.getPassword())){
                return new ResponseEntity<>("Password matched, login successful, go to main", HttpStatus.OK);
            }
            return new ResponseEntity<>("Password incorrect, login unsuccessful, retry password", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("email not found", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        String email = forgotPasswordRequestDTO.getEmail();

        String code = linkService.generateLink();
        linkService.addLinkDetails(email, code);

        String link = siteURL + "/auth/login/verify-login-reset-password-link?code=" + code;
        emailService.sendResetPasswordLink(email, link);
        return new ResponseEntity<>("reset password link sent successfully", HttpStatus.OK);
    } // try to set up subject, body here itself and pass as object to emailService

    @Override
    public ResponseEntity<String> verifyLoginResetPasswordLink(String code) {
        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
            return new ResponseEntity<>("reset password link verified successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid or Expired Link", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> sendLoginCode(UserTempLoginRequestDTO userTempLoginRequestDTO) {
        String email = userTempLoginRequestDTO.getEmail();

        String otp = otpService.generateOTP();
        otpService.addOTPDetails(email, otp);

        String code = linkService.generateLink();
        linkService.addLinkDetails(email, code);


        String link = siteURL + "/auth/login/verify-temp-login-link?code=" + code;
        emailService.sendOTPLinkEmail(email, otp, link);
        return new ResponseEntity<>("login code sent successful", HttpStatus.OK);
    } // try to set up subject, body here itself and pass as object to emailService, this is to have single sendOTPEmail function in emailService

    @Override
    public ResponseEntity<String> verifyTempLoginLink(String code) {
        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
            return new ResponseEntity<>("Login Temp Link verified successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid or Expired Link", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> verifyTempLoginOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        boolean isVerified = otpService.verifyOTP(verifyOTPRequestDTO);
        System.out.println("im here 7");

        if(isVerified){
            System.out.println("im here 8");

            return new ResponseEntity<>("temp login otp verified successfully", HttpStatus.OK);
        }
        System.out.println("im here 9");

        return new ResponseEntity<>("temp login otp invalid or expired", HttpStatus.BAD_REQUEST);
    }
}
