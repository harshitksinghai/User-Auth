package com.harshitksinghai.UserEntry.Repositories;

import com.harshitksinghai.UserEntry.Models.LinkVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkVerificationRepository extends JpaRepository<LinkVerification, Long> {
    Optional<LinkVerification> findByCode(String code);

    void deleteByCode(String code);

    void deleteByExpirationTimeBefore(LocalDateTime now);
}
