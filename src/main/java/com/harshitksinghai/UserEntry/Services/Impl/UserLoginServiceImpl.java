package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.JwtResponseDTO;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    private final Logger LOG = LoggerFactory.getLogger(UserLoginServiceImpl.class);
    @Autowired
    UserService userService;

    @Autowired
    LinkService linkService;

    @Autowired
    EmailService emailService;

    @Autowired
    OTPService otpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${site.url}")
    private String siteURL;

    @Override
    public ResponseEntity<UserLoginResponseDTO> verifyPassword(UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        if (userLoginRequestDTO == null) {
            userLoginResponseDTO.setMessage("There must be enough data to proceed further.");
            userLoginResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }

        Optional<User> userOpt = userService.findByEmail(userLoginRequestDTO.getEmail());
        LOG.info(" user fetched from DB " + userOpt.toString());

        if (userOpt.isEmpty()) {
            userLoginResponseDTO.setMessage("email not found");
            userLoginResponseDTO.setSuccess(false);
        }
        //User user = userOpt.get();
        String jwtToken = null;
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            jwtToken = jwtUtils.generateToken(userLoginRequestDTO.getEmail());
            userLoginResponseDTO.setMessage("email and password verified successfully");
            userLoginResponseDTO.setSuccess(true);
            userLoginResponseDTO.setJwtToken(jwtToken);
        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.ACCEPTED);
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
