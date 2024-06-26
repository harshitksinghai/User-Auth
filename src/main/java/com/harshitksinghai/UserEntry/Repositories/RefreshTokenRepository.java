package com.harshitksinghai.UserEntry.Repositories;

import com.harshitksinghai.UserEntry.Models.RefreshToken;
import com.harshitksinghai.UserEntry.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
