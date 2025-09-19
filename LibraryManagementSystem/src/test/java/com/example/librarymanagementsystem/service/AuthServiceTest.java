package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.util.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidationHelper validationHelper;

    // ADD THIS: Mock the PasswordEncoder that AuthService expects
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private BCryptPasswordEncoder realPasswordEncoder; // For creating test data

    @BeforeEach
    void setUp() {
        // Keep a real encoder for test setup only
        realPasswordEncoder = new BCryptPasswordEncoder();

        // Create test user with hashed password
        testUser = new User();
        testUser.setId("test-user-id");
        testUser.setUsername("testuser");
        testUser.setPasswordHash(realPasswordEncoder.encode("password123"));
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void testLoginSuccess() {
        // Arrange - set up test data and mock behavior
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        // CRITICAL: Mock the password encoder to return true for correct password
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);

        // Act - execute the method being tested
        Optional<User> result = authService.login("testuser", "password123");

        // Assert - verify the expected outcome
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());

        // Verify mock interactions
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPasswordHash());
    }

    @Test
    void testLoginFailureInvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        // Mock password encoder to return false for wrong password
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        // Act
        Optional<User> result = authService.login("testuser", "wrongpassword");

        // Assert
        assertFalse(result.isPresent());
        verify(passwordEncoder).matches("wrongpassword", testUser.getPasswordHash());
    }

    @Test
    void testLoginFailureUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = authService.login("nonexistent", "password123");

        // Assert
        assertFalse(result.isPresent());
        // Password encoder should not be called if user not found
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        when(validationHelper.validateUserRegistration(anyString(), anyString(), anyString()))
                .thenReturn(new ArrayList<>()); // No validation errors
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        // Mock password encoding for registration
        when(passwordEncoder.encode("password123")).thenReturn("mocked-hash");

        // Act
        User result = authService.register("newuser", "new@example.com", "password123");

        // Assert
        assertNotNull(result);
        verify(validationHelper).validateUserRegistration("newuser", "new@example.com", "password123");
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterFailureUsernameExists() {
        // Arrange
        when(validationHelper.validateUserRegistration(anyString(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register("existinguser", "new@example.com", "password123");
        });

        assertEquals("Username already exists", exception.getMessage());
        // Password should not be encoded if username already exists
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testRegisterFailureEmailExists() {
        // Arrange
        when(validationHelper.validateUserRegistration(anyString(), anyString(), anyString()))
                .thenReturn(new ArrayList<>());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register("newuser", "existing@example.com", "password123");
        });

        assertEquals("Email already exists", exception.getMessage());
        // Password should not be encoded if email already exists
        verify(passwordEncoder, never()).encode(anyString());
    }
}