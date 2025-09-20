package com.example.librarymanagementsystem.util;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @Component tells Spring to create and manage this as a bean
// This utility helps with authentication checks across all controllers
@Component
public class AuthHelper {

    private final UserRepository userRepository;

    // Constructor injection - Spring provides UserRepository
    public AuthHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get current logged-in user from session
    public Optional<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return Optional.empty();

        String stored = (String) session.getAttribute("userId");
        if (stored == null || stored.isEmpty()) return Optional.empty();

        // try as ID first
        Optional<User> byId = userRepository.findById(stored);
        if (byId.isPresent()) return byId;

        // fallback: treat stored value as username
        return userRepository.findByUsername(stored);
    }

    // Check if current user has admin role
    public boolean isAdmin(HttpServletRequest request) {
        Optional<User> userOpt = getCurrentUser(request);
        return userOpt.isPresent() && userOpt.get().getRole() == UserRole.ADMIN;
    }

    // Check if current user is logged in
    public boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request).isPresent();
    }

    // Check if current user can access a specific resource (owns it or is admin)
    public boolean canAccessResource(String resourceUserId, HttpServletRequest request) {
        Optional<User> currentUser = getCurrentUser(request);
        if (currentUser.isEmpty()) {
            return false;
        }

        // Admin can access any resource
        if (currentUser.get().getRole() == UserRole.ADMIN) {
            return true;
        }

        // User can only access their own resources
        return currentUser.get().getId().equals(resourceUserId);
    }

    // Get current user ID from session (convenience method)
    public Optional<String> getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        String userId = (String) session.getAttribute("userId");
        return Optional.ofNullable(userId);
    }

    // Validate pagination parameters
    public void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }
    }

    // Apply pagination to any list
    public <T> List<T> applyPagination(List<T> items, int page, int size) {
        validatePagination(page, size);

        int start = page * size;
        int end = Math.min(start + size, items.size());

        if (start >= items.size()) {
            return new ArrayList<>();
        }

        return items.subList(start, end);
    }
}