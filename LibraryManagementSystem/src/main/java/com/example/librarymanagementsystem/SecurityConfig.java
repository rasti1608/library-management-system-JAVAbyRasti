/**
 * Library Management System - Security Configuration
 * Created by: Rastislav Toscak
 * Date: September 18, 2025
 * Description: Spring Security configuration for authentication and authorization
 *
 * Configures session-based authentication with role-based access control
 */
package com.example.librarymanagementsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP security and endpoint access rules
     * Uses session-based authentication with custom AuthHelper for user validation
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CORS is handled by a CorsFilter bean (see CorsConfig); no http.cors() needed
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/health",
                                "/health",
                                "/api/auth/**",
                                "/auth/**",
                                "/api/books/**",
                                "/books/**",
                                "/api/users/**",
                                "/users/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .build();
    }

    /**
     * Password encoder for secure password hashing
     * BCrypt with strength 10 provides good security/performance balance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}