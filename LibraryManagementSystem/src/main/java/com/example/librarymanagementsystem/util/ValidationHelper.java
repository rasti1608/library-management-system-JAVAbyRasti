package com.example.librarymanagementsystem.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// @Component makes this a Spring-managed bean available for dependency injection
// This utility centralizes validation logic to avoid duplicating validation rules
@Component
public class ValidationHelper {

    // Email pattern for basic email validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Validate user registration data
    public List<String> validateUserRegistration(String username, String email, String password) {
        List<String> errors = new ArrayList<>();

        // Username validation
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        } else if (username.trim().length() < 3) {
            errors.add("Username must be at least 3 characters");
        } else if (username.trim().length() > 20) {
            errors.add("Username cannot exceed 20 characters");
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            errors.add("Username can only contain letters, numbers, and underscores");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.add("Email format is invalid");
        }

        // Password validation (from business requirements)
        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
        } else {
            if (password.length() < 8) {
                errors.add("Password must be at least 8 characters");
            }
            if (!password.matches(".*[a-zA-Z].*")) {
                errors.add("Password must contain at least one letter");
            }
            if (!password.matches(".*[0-9].*")) {
                errors.add("Password must contain at least one number");
            }
        }

        return errors;
    }

    // Validate book data
    public List<String> validateBook(String title, String author, String genre) {
        List<String> errors = new ArrayList<>();

        // Title validation
        if (title == null || title.trim().isEmpty()) {
            errors.add("Title is required");
        } else if (title.trim().length() > 100) {
            errors.add("Title cannot exceed 100 characters");
        }

        // Author validation
        if (author == null || author.trim().isEmpty()) {
            errors.add("Author is required");
        } else if (author.trim().length() > 50) {
            errors.add("Author name cannot exceed 50 characters");
        }

        // Genre validation (optional but limited length)
        if (genre != null && genre.trim().length() > 30) {
            errors.add("Genre cannot exceed 30 characters");
        }

        return errors;
    }

    // Validate user profile update
    public List<String> validateUserUpdate(String username, String email) {
        List<String> errors = new ArrayList<>();

        // Username validation (same as registration but password not required)
        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        } else if (username.trim().length() < 3) {
            errors.add("Username must be at least 3 characters");
        } else if (username.trim().length() > 20) {
            errors.add("Username cannot exceed 20 characters");
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            errors.add("Username can only contain letters, numbers, and underscores");
        }

        // Email validation
        if (email == null || email.trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.add("Email format is invalid");
        }

        return errors;
    }

    // Validate password change (optional password update)
    public List<String> validatePasswordChange(String password) {
        List<String> errors = new ArrayList<>();

        if (password != null && !password.isEmpty()) {
            if (password.length() < 8) {
                errors.add("Password must be at least 8 characters");
            }
            if (!password.matches(".*[a-zA-Z].*")) {
                errors.add("Password must contain at least one letter");
            }
            if (!password.matches(".*[0-9].*")) {
                errors.add("Password must contain at least one number");
            }
        }

        return errors;
    }

    // Validate pagination parameters
    public List<String> validatePagination(int page, int size) {
        List<String> errors = new ArrayList<>();

        if (page < 0) {
            errors.add("Page number cannot be negative");
        }

        if (size < 1) {
            errors.add("Page size must be at least 1");
        } else if (size > 100) {
            errors.add("Page size cannot exceed 100");
        }

        return errors;
    }
}