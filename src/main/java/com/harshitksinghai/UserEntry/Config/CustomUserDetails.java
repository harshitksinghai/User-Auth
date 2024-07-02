package com.harshitksinghai.UserEntry.Config;

import com.harshitksinghai.UserEntry.Models.User;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Getter
public class CustomUserDetails extends User implements UserDetails {
    private final Logger LOG = LoggerFactory.getLogger(CustomUserDetails.class);
    private String email;
    private String password;

    public CustomUserDetails(User user){
        LOG.info("Inside Custom User Details ...................");
        email = user.getEmail();
        password = user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
