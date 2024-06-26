package com.harshitksinghai.UserEntry.Config;

import com.harshitksinghai.UserEntry.Models.User;
import com.harshitksinghai.UserEntry.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

// When a user attempts to authenticate, Spring Security calls the loadUserByUsername method of CustomUserDetailsService with the provided email.
// CustomUserDetailsService retrieves the user from the database using UserRepository.
// If the user exists, it creates a CustomUserDetails object with the user's details and returns it.
// The CustomUserDetails object is used by Spring Security to get the user's credentials and other necessary information for authentication.

@Component
public class CustomUserDetailsServiceImpl implements UserDetailsService { // The main purpose of UserDetailsService is to allow Spring Security to load user-specific data during authentication. It serves as a bridge between your application's user model and Spring Security.
    private final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOG.debug("Entering in loadUserByUsername Method...");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()){
            LOG.error("Username not found: " + email);
            throw new UsernameNotFoundException("could not found user..!!");
        }

        User user = userOpt.get();

        LOG.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }
}
