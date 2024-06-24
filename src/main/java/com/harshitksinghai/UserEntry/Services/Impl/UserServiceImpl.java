package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Repositories.UserRepository;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public void saveUserDetails(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }
}
