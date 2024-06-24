package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Models.LinkVerification;
import com.harshitksinghai.UserEntry.Repositories.LinkVerificationRepository;
import com.harshitksinghai.UserEntry.Services.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    LinkVerificationRepository linkVerificationRepository;

    @Override
    public String generateLink() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public void addLinkDetails(String email, String code) {
        LinkVerification linkVerification = new LinkVerification();
        linkVerification.setEmail(email);
        linkVerification.setCode(code);
        linkVerification.setExpirationTime(LocalDateTime.now().plusMinutes(5));

        linkVerificationRepository.save(linkVerification);
    }

    @Override
    @Transactional
    public ResponseEntity<String> verifyLink(String code) {
        Optional<LinkVerification> linkVerificationOpt = linkVerificationRepository.findByCode(code);
        if(!linkVerificationOpt.isPresent()){
            throw new IllegalArgumentException("magic-link not found");
        }

        LinkVerification linkVerification = linkVerificationOpt.get();
        String email = linkVerification.getEmail();

        if(linkVerification.getExpirationTime().isAfter(LocalDateTime.now())){
            linkVerificationRepository.deleteByCode(code);
            return new ResponseEntity<>(email, HttpStatus.OK);
        }
        else{
            linkVerificationRepository.deleteByCode(code);
            throw new IllegalArgumentException("Invalid or expired link.");
        }

    }
}
