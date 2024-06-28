package com.harshitksinghai.UserEntry.Config.Filter;

import com.harshitksinghai.UserEntry.Config.CustomUserDetailsServiceImpl;
import com.harshitksinghai.UserEntry.Config.JwtUtility.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// The JwtAuthFilter class is a custom filter that extends OncePerRequestFilter, ensuring it runs once per request. This filter intercepts incoming HTTP requests to validate the JWT token provided in the Authorization header and sets up the security context accordingly.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final Logger LOG = LoggerFactory.getLogger(JwtAuthFilter.class);
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    CustomUserDetailsServiceImpl customUserDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LOG.info("Inside the doFilterInternal Method");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try {
                userEmail = this.jwtUtils.extractUsername(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the userEmail !!");
                e.printStackTrace();
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            logger.info("Invalid Header Value !! ");
        }

        // When a user successfully logs in, an Authentication object is created and stored in the SecurityContextHolder. This object contains the principal (user details), credentials (password), and authorities (roles and permissions).

        LOG.info("Inside the doFilterInternal Method after token extraction");
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customUserDetailsServiceImpl.loadUserByUsername(userEmail);
                if(jwtUtils.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            else {
                logger.info("Validation fails !!");
            }
        }

        filterChain.doFilter(request, response);
    }
}
