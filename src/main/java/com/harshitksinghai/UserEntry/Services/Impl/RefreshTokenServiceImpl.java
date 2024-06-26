package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Repositories.RefreshTokenRepository;
import com.harshitksinghai.UserEntry.Repositories.UserRepository;
import com.harshitksinghai.UserEntry.Services.RefreshTokenService;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Override
    public RefreshToken createRefreshToken(String email) {
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
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    @Override
    public void delete(RefreshToken existingToken) {
        refreshTokenRepository.delete(existingToken);
    }
}
