package com.harshitksinghai.UserEntry.DTO.RequestDTO;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSignUpRequestDTO {
    String email;
}
