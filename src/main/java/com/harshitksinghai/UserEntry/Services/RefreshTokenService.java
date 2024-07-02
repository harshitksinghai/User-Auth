package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.Models.RefreshToken;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenService {
    @Transactional
    RefreshToken createRefreshToken(String email);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    @Transactional
    void delete(RefreshToken existingToken);

    @Transactional
    void deleteByToken(String refreshToken);
}
