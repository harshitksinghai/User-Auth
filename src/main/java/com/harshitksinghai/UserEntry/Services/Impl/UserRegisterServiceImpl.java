package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserOnBoardRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserSignUpRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    @Autowired
    OTPService otpService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    LinkService linkService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${site.url}")
    private String siteURL;

    @Override
    public ResponseEntity<String> signUpUser(UserSignUpRequestDTO userSignUpRequestDTO) {
        String email = userSignUpRequestDTO.getEmail();
        if(userService.findByEmail(email).isPresent()){
            return new ResponseEntity<>("email already exists", HttpStatus.BAD_REQUEST);
        }
        String otp = otpService.generateOTP();
        otpService.addOTPDetails(email, otp);

        String code = linkService.generateLink();
        linkService.addLinkDetails(email, code);


        String link = siteURL + "/auth/register/verify-link?code=" + code;
        emailService.sendOTPLinkEmail(email, otp, link);
        return new ResponseEntity<>("signup successful", HttpStatus.OK);
    } // try to set up subject, body here itself and pass as object to emailService, this is to have single sendOTPEmail function in emailService

    @Override
    public ResponseEntity<String> verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        String email = verifyOTPRequestDTO.getEmail();
        if (userService.findByEmail(email).isPresent()) {
            return new ResponseEntity<>("email already verified, you can start the onboarding process", HttpStatus.BAD_REQUEST);
        }
        boolean isVerified = otpService.verifyOTP(verifyOTPRequestDTO);
        System.out.println("im here 10");
        if (isVerified) {
            System.out.println("im here 11");

            User user = new User();
            user.setIsVerified(true);
            user.setEmail(email);

            userService.saveUserDetails(user);

            return new ResponseEntity<>("otp verified successfully", HttpStatus.OK);
        }
        System.out.println("im here 12");

        return new ResponseEntity<>("Invalid or Expired otp", HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<String> onBoardUser(UserOnBoardRequestDTO userOnBoardRequestDTO) {
        Optional<User> userOpt = userService.findByEmail(userOnBoardRequestDTO.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getIsVerified()) {
                user.setUsername(userOnBoardRequestDTO.getUsername());
                String encryptedPassword = passwordEncoder.encode(userOnBoardRequestDTO.getPassword());
                user.setPassword(encryptedPassword); // password encryption not yet implemented
                userService.saveUserDetails(user);
            } else {
                userService.deleteUser(user.getEmail());
                throw new IllegalArgumentException("User not verified.");
            }
        } else {
            throw new IllegalArgumentException("User not found.");
        }
        return new ResponseEntity<>("user successfully registered with application", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> verifyLink(String code) {
        String email = "";
        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            email = responseEntity.getBody();
        }
        if (userService.findByEmail(email).isPresent()) {
            return new ResponseEntity<>("email already verified, you can start the onboarding process", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setIsVerified(true);
        user.setEmail(email);

        userService.saveUserDetails(user);
        return new ResponseEntity<>("link verified successfully", HttpStatus.OK);
    }

}

