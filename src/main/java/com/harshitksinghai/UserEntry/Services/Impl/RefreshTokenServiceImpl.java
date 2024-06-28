package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Repositories.RefreshTokenRepository;
import com.harshitksinghai.UserEntry.Repositories.UserRepository;
import com.harshitksinghai.UserEntry.Services.RefreshTokenService;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final Logger LOG = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Override
    public RefreshToken createRefreshToken(String email) {
        LOG.info("inside createRefreshToken in RefreshTokenServiceImpl");
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (refreshTokenRepository.findByToken(token).isPresent());

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(token)
                .expiryDate(Instant.now().plusMillis(10*60*1000)) // 10minutes
                .email(email)
                .build();

        return refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token){
        LOG.info("inside findByToken in RefreshTokenServiceImpl");
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        LOG.info("inside verifyExpiration in RefreshTokenServiceImpl");

        if(refreshToken.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(refreshToken.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return refreshToken;
    }

    @Override
    public void delete(RefreshToken existingToken) {
        LOG.info("inside delete in RefreshTokenServiceImpl");

        refreshTokenRepository.delete(existingToken);
    }
}
