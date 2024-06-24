package com.harshitksinghai.UserEntry.Services;

import com.harshitksinghai.UserEntry.Models.User;

import java.util.Optional;

public interface UserService {
    void saveUserDetails(User user);

    Optional<User> findByEmail(String email);

    void deleteUser(String email);
}
