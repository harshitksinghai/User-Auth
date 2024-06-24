package com.harshitksinghai.UserEntry.DTO.RequestDTO;

import lombok.Data;

@Data
public class VerifyOTPRequestDTO {
    private String email;
    private String otp;
}
