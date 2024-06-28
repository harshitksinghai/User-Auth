package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.DTO.ResponseDTO.UserLoginResponseDTO;
import com.harshitksinghai.UserEntry.Models.User;

public interface TokenService {
    UserLoginResponseDTO generateJwtAndRefreshToken(User user);
}
