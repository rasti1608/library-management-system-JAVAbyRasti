package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.util.UuidGenerator;
import com.example.librarymanagementsystem.util.ValidationHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service tells Spring this is a business logic component
// Spring will create instance and inject dependencies automatically
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationHelper validationHelper;  // Add this line

    // Constructor injection - Spring finds and injects all dependencies
    public AuthService(UserRepository userRepository, ValidationHelper validationHelper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.validationHelper = validationHelper;
        this.passwordEncoder = passwordEncoder;  // Use injected encoder, don't create new one
    }

    // User login - validate credentials
    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Found user: " + user.getUsername());
            System.out.println("Stored hash: " + user.getPasswordHash());
            System.out.println("Input password: " + password);

            boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
            System.out.println("Password matches: " + matches);



            if (matches) {
                return Optional.of(user);
            }
        } else {
            System.out.println("User not found");
        }
        return Optional.empty();
    }

    // User registration - create new account
    public User register(String username, String email, String password) {
        // Use ValidationHelper for comprehensive validation
        List<String> validationErrors = validationHelper.validateUserRegistration(username, email, password);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors));
        }

        // Process the input parameters
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();

        // Check for existing username
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new RuntimeException("Username already exists");
        }

        // Check for existing email
        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user with hashed password
        User newUser = new User();                     // use no-args
        newUser.setId(UuidGenerator.generate());       // generate ID
        newUser.setUsername(username);

        // hash the password inline instead of using "hashed" variable
        newUser.setPasswordHash(passwordEncoder.encode(password));

        newUser.setEmail(email);
        newUser.setRole(UserRole.USER);
        newUser.setMustChangePassword(false);
        newUser.setProtected(false);

        return userRepository.save(newUser);
    }
}