package com.harshitksinghai.UserEntry.Repositories;

import com.harshitksinghai.UserEntry.Models.OTPVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {
    void deleteByEmail(String email);

    Optional<OTPVerification> findByOtp(String otp);

    void deleteByOtp(String otp);

    void deleteByExpirationTimeBefore(LocalDateTime now);
}
