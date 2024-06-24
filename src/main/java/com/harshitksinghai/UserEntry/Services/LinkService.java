package com.harshitksinghai.UserEntry.Services;

import org.springframework.http.ResponseEntity;

public interface LinkService {
    String generateLink();

    void addLinkDetails(String email, String link);

    ResponseEntity<String> verifyLink(String code);
}
