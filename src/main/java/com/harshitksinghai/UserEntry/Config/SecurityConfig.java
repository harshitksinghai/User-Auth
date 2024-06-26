package com.harshitksinghai.UserEntry.Config;

import com.harshitksinghai.UserEntry.Config.Filter.JwtAuthFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

// Your SecurityConfig class is responsible for configuring Spring Security within your application.

@Configuration // Indicates that this class provides bean definitions and should be processed by the Spring container to generate Spring beans.
@EnableWebSecurity // Enables Spring Security's web security support and provides the Spring MVC integration. This annotation is essential to enable security features for web applications.
@EnableMethodSecurity // Enables Spring Security's method-level security, allowing you to use annotations like @Secured, @PreAuthorize, @PostAuthorize, etc., to secure methods in your application.
public class SecurityConfig {
    private final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private JwtAuthFilter authFilter;

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsServiceImpl();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{ // Configures the security filters and rules using HttpSecurity.
        LOG.info("SecurityConfig -> Inside the SecurityFilterChain......");
        http.csrf(csrf ->csrf.disable()) // .csrf(csrf -> csrf.disable()): Disables CSRF protection. CSRF (Cross-Site Request Forgery) protection is disabled because JWT (JSON Web Token) authentication typically does not use cookies and is stateless.
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")) // Configures logout behavior to delete the JSESSIONID cookie upon logout. This is important for clearing session-related information.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // In JWT-based authentication, sessions are not stored on the server; each request must contain all necessary information (typically in the JWT itself).


        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class); // Adds authFilter (JwtAuthFilter in your case) before UsernamePasswordAuthenticationFilter. This ensures that the JWT authentication filter (JwtAuthFilter) is executed before Spring Security's default form-based authentication filter.

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        LOG.info("SecurityConfig -> Inside AuthenticationProvider..........................");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(); // It connects the UserDetailsService (CustomUserDetailsService) and PasswordEncoder (BCryptPasswordEncoder) to perform authentication based on database-stored user details.
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { // The AuthenticationManager is a core interface in Spring Security that handles authentication requests. This bean is necessary for authenticating users during login attempts.
        LOG.info("SecurityConfig -> Inside AuthenticationManager.........");
        return config.getAuthenticationManager();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    } // This method registers HttpSessionEventPublisher, which publishes HttpSessionEvent events to Spring's event infrastructure. This is required to support session-related events in a Spring Security-managed application.
}
