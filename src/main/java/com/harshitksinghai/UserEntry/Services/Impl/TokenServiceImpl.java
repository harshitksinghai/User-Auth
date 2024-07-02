package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Services.RefreshTokenService;
import com.harshitksinghai.UserEntry.Services.TokenService;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    private final Logger LOG = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    UserService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    public UserLoginResponseDTO generateJwtAndRefreshToken(User user){
        LOG.info("inside generateJwtAndRefreshToken in TokenServiceImpl");
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();


            LOG.info("cleared {authenticationManager} in verifyTempLoginLink");
            RefreshToken existingToken = user.getRefreshToken();
            if(existingToken != null){
                user.setRefreshToken(null);
                refreshTokenService.delete(existingToken);
                userService.saveUserDetails(user);
            }

            String jwtToken = null;
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
            user.setRefreshToken(refreshToken);
            userService.saveUserDetails(user);

            jwtToken = jwtUtils.generateToken(user.getEmail());

            if(jwtToken != null){
                userLoginResponseDTO.setSuccess(true);
                userLoginResponseDTO.setJwtToken(jwtToken);
                userLoginResponseDTO.setRefreshToken(refreshToken.getToken());
            }

        return userLoginResponseDTO;
    }
}
