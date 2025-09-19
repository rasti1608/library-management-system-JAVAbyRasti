package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service for business logic component
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Constructor injection with multiple dependencies
    public UserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Get all users (admin only)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Find user by ID
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    // Update user profile (can edit own profile or admin can edit any)
    public User updateUser(String id, String username, String email, String password) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Check if username is taken by another user
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }

        // Check if email is taken by another user
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already taken");
        }

        user.setUsername(username);
        user.setEmail(email);

        // Update password if provided
        if (password != null && !password.trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    // Promote user to admin (admin only)
    public User promoteToAdmin(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }

    // Demote admin to user (admin only)
    public User demoteToUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Business rule: cannot demote protected admin accounts
        if (user.isProtected()) {
            throw new RuntimeException("Cannot demote protected admin account");
        }

        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    // Delete user account (admin only)
    public void deleteUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Business rule: cannot delete protected accounts
        if (user.isProtected()) {
            throw new RuntimeException("Cannot delete protected admin account");
        }

        // Business rule: cannot delete users with active rentals
        boolean hasActiveRentals = !rentalRepository.findByUserId(id).isEmpty();
        if (hasActiveRentals) {
            throw new RuntimeException("Cannot delete user with active rentals. User must return all books first.");
        }

        userRepository.delete(id);
    }
}