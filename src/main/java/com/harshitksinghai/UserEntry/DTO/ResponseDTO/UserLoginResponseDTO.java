package com.harshitksinghai.UserEntry.DTO.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginResponseDTO {
    private boolean success;
    private String message;
    private String jwtToken;
    private String refreshToken;

}
