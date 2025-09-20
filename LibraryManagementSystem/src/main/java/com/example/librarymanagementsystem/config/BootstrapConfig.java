package com.example.librarymanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;   // <-- import enum
import com.example.librarymanagementsystem.repository.UserRepository;

@Configuration
public class BootstrapConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initAdmin() {
        return args -> {
            final String username = "admin";
            final String rawPassword = "admin123";

            boolean exists = userRepository.findAll().stream()
                    .anyMatch(u -> username.equalsIgnoreCase(u.getUsername()));

            if (!exists) {
                User u = new User();
                u.setUsername(username);
                u.setPasswordHash(passwordEncoder.encode(rawPassword));
                u.setRole(UserRole.ADMIN);                // <-- enum, not string
                userRepository.save(u);
                System.out.println(">> Seeded default admin/admin123");
            }
        };
    }
}
