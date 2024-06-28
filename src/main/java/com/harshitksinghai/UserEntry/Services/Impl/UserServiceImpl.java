package com.harshitksinghai.UserEntry.Services.Impl;

import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Repositories.UserRepository;
import com.harshitksinghai.UserEntry.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {
    private final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public void saveUserDetails(User user) {
        LOG.info("inside saveUserDetails in UserServiceImpl");
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LOG.info("inside findByEmail in UserServiceImpl");
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        LOG.info("inside deleteUser in UserServiceImpl");
        userRepository.deleteByEmail(email);
    }
}
