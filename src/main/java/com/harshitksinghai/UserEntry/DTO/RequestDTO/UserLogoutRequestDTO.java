package com.harshitksinghai.UserEntry.DTO.RequestDTO;

import lombok.Data;

@Data
public class UserLogoutRequestDTO {
    private String jwtToken;
    private String refreshToken;
    private String email;
}
