package com.harshitksinghai.UserEntry.DTO.RequestDTO;

import lombok.Data;

@Data
public class UserLoginRequestDTO {
    private String email;
    private String password;
}
