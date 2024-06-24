package com.harshitksinghai.UserEntry.Repositories;

import com.harshitksinghai.UserEntry.Models.LinkVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkVerificationRepository extends JpaRepository<LinkVerification, Long> {
    Optional<LinkVerification> findByCode(String code);

    void deleteByEmail(String email);

    void deleteByCode(String code);
}
