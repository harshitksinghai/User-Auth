package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserOnBoardRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.UserSignUpRequestDTO;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.VerifyOTPRequestDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {
    private final Logger LOG = LoggerFactory.getLogger(UserRegisterServiceImpl.class);

    @Autowired
    OTPService otpService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    LinkService linkService;

    @Autowired
    TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${site.url}")
    private String siteURL;

    @Override
    public ResponseEntity<String> signUpUser(UserSignUpRequestDTO userSignUpRequestDTO) {
        LOG.info("inside signUpUser in UserRegisterServiceImpl");
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
    public ResponseEntity<UserLoginResponseDTO> verifyOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        LOG.info("inside verifyOTP in UserRegisterServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        String email = verifyOTPRequestDTO.getEmail();
        if (userService.findByEmail(email).isPresent()) {
            userLoginResponseDTO.setMessage("email already verified, you can start the onboarding process");
            userLoginResponseDTO.setSuccess(true);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }
        boolean isVerified = otpService.verifyOTP(verifyOTPRequestDTO);

        if (isVerified) {
            LOG.info("adding partial user details (email and isVerified) to db");
            User user = new User();
            user.setIsVerified(true);
            user.setEmail(email);
            userService.saveUserDetails(user);

            LOG.info("jwtToken and refreshToken generation about to start");
            Optional<User> userOpt = userService.findByEmail(verifyOTPRequestDTO.getEmail());

            if (userOpt.isPresent()) {
                userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
            } else {

                LOG.info("user email not in database");
                userLoginResponseDTO.setMessage("user email not in database");
                userLoginResponseDTO.setSuccess(false);
                return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
            }
            userLoginResponseDTO.setMessage("otp verified successfully");
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
        }
        LOG.info("Invalid or Expired otp");
        userLoginResponseDTO.setMessage("Invalid or Expired otp");
        userLoginResponseDTO.setSuccess(false);
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
    }


    @Override
    public ResponseEntity<String> onBoardUser(UserOnBoardRequestDTO userOnBoardRequestDTO) {
        LOG.info("inside onBoardUser in UserRegisterServiceImpl");

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
    public ResponseEntity<UserLoginResponseDTO> verifyLink(String code) {
        LOG.info("inside verifyLink in UserRegisterServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        String email = "";
        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            email = responseEntity.getBody();
        }
        else {
            userLoginResponseDTO.setMessage("Invalid or Expired Link");
            userLoginResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }
        if (userService.findByEmail(email).isPresent()) {
            userLoginResponseDTO.setMessage("email already verified, you can start the onboarding process");
            userLoginResponseDTO.setSuccess(true);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }

        LOG.info("adding partial user details (email and isVerified) to db");
        User user = new User();
        user.setIsVerified(true);
        user.setEmail(email);
        userService.saveUserDetails(user);

        LOG.info("jwtToken and refreshToken generation about to start");
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
        } else {
            LOG.info("user email not in database");
            userLoginResponseDTO.setMessage("user email not in database");
            userLoginResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }

        userLoginResponseDTO.setMessage("link verified successfully");
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }

}

