package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import com.harshitksinghai.UserEntry.DTO.RequestDTO.*;
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

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    TokenService tokenService;

    @Value("${site.url}")
    private String siteURL;

    @Override
    public ResponseEntity<UserLoginResponseDTO> verifyPassword(UserLoginRequestDTO userLoginRequestDTO) {
        LOG.info("inside verifyPassword in UserLoginServiceImpl");
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
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword()));
        LOG.info("verify password - passed authenticationManager code");

        if (authentication.isAuthenticated()) {
            userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
        } else {
            LOG.info("email and password did not match");
            userLoginResponseDTO.setMessage("email and password did not match");
            userLoginResponseDTO.setSuccess(false);
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
        }
        userLoginResponseDTO.setMessage("email and password verified successfully");
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<String> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        LOG.info("inside forgotPassword in UserLoginServiceImpl");

        String email = forgotPasswordRequestDTO.getEmail();

        String code = linkService.generateLink();
        linkService.addLinkDetails(email, code);

        String link = siteURL + "/auth/login/verify-login-reset-password-link?code=" + code;
        emailService.sendResetPasswordLink(email, link);
        return new ResponseEntity<>("reset password link sent successfully", HttpStatus.OK);
    } // try to set up subject, body here itself and pass as object to emailService

    @Override
    public ResponseEntity<UserLoginResponseDTO> verifyLoginResetPasswordLink(String code) {
        LOG.info("inside verifyLoginResetPasswordLink in UserLoginServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
            String email = responseEntity.getBody();

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
            } else {
                LOG.info("user email not in database");
                throw new UsernameNotFoundException("invalid user request..!!");
            }
            userLoginResponseDTO.setMessage("reset password link verified successfully");
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
        }

        userLoginResponseDTO.setMessage("Invalid or Expired Link");
        userLoginResponseDTO.setSuccess(false);
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> sendLoginCode(UserTempLoginRequestDTO userTempLoginRequestDTO) {
        LOG.info("inside SendLoginCode in UserLoginServiceImpl");

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
    public ResponseEntity<UserLoginResponseDTO> verifyTempLoginLink(String code) {
        LOG.info("inside verifyTempLoginLink in UserLoginServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        ResponseEntity<String> responseEntity = linkService.verifyLink(code);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
            LOG.info("just entered HttpStatus.OK.equals(responseEntity.getStatusCode");
            String email = responseEntity.getBody();

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
            } else {
                LOG.info("user email not in database");
                userLoginResponseDTO.setMessage("user email not in database");
                userLoginResponseDTO.setSuccess(false);
                return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
            }

            userLoginResponseDTO.setMessage("Login Temp Link verified successfully");
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
        }
        userLoginResponseDTO.setMessage("Invalid or Expired Link");
        userLoginResponseDTO.setSuccess(false);
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<UserLoginResponseDTO> verifyTempLoginOTP(VerifyOTPRequestDTO verifyOTPRequestDTO) {
        LOG.info("inside verifyTempLoginOTP in UserLoginServiceImpl");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        boolean isVerified = otpService.verifyOTP(verifyOTPRequestDTO);

        if(isVerified){
            LOG.info("otp verified successfully, (in UserLoginServiceImpl verifyTempLoginOTP)");
            Optional<User> userOpt = userService.findByEmail(verifyOTPRequestDTO.getEmail());

            if (userOpt.isPresent()) {
                userLoginResponseDTO = tokenService.generateJwtAndRefreshToken(userOpt.get());
            } else {

                LOG.info("user email not in database");
                userLoginResponseDTO.setMessage("user email not in database");
                userLoginResponseDTO.setSuccess(false);
                return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
            }

            LOG.info("temp login otp verified successfully");
            userLoginResponseDTO.setMessage("temp login otp verified successfully");
            return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
        }
        LOG.info("temp login otp invalid or expired");
        userLoginResponseDTO.setMessage("temp login otp invalid or expired");
        userLoginResponseDTO.setSuccess(false);
        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.BAD_REQUEST);
    }
}
