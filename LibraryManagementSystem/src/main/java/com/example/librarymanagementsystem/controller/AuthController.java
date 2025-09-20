package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.dto.LoginRequest;
import com.example.librarymanagementsystem.model.dto.RegisterRequest;
import com.example.librarymanagementsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

// @Tag groups related endpoints together in Swagger UI
@Tag(name = "Authentication", description = "User authentication and session management")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // @Operation provides endpoint description and summary for Swagger
    @Operation(
            summary = "User login",
            description = "Authenticate user credentials and create session"
    )
    // @ApiResponses documents possible HTTP response codes
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Optional<User> userOpt = authService.login(request.getUsername(), request.getPassword());

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Add these debug lines here:
                System.out.println("User ID: " + user.getId());
                System.out.println("User username: " + user.getUsername());
                System.out.println("User email: " + user.getEmail());
                System.out.println("User role: " + user.getRole());

                try {
                    // Create HTTP session for user
                    System.out.println("Creating session for user: " + user.getId());
                    HttpSession session = httpRequest.getSession(true);
                    System.out.println("Session created: " + session.getId());

                    session.setAttribute("userId", user.getId());
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole().toString());
                    System.out.println("Session attributes set successfully");

                    return ResponseEntity.ok(Map.of(
                            "message", "Login successful",
                            "user", Map.of(
                                    "id", user.getId(),
                                    "username", user.getUsername(),
                                    "email", user.getEmail(),
                                    "role", user.getRole()
                            )
                    ));
                } catch (Exception sessionError) {
                    System.out.println("Session creation failed: " + sessionError.getMessage());
                    sessionError.printStackTrace();
                    return ResponseEntity.status(500).body(Map.of("error", "Session creation failed: " + sessionError.getMessage()));
                }
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "User registration",
            description = "Create new user account with USER role"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );

            return ResponseEntity.status(201).body(Map.of(
                    "message", "Registration successful",
                    "user", Map.of(
                            "id", newUser.getId(),
                            "username", newUser.getUsername(),
                            "email", newUser.getEmail(),
                            "role", newUser.getRole()
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "User logout",
            description = "Invalidate current user session"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
}