package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.dto.PagedResponse;
import com.example.librarymanagementsystem.service.RentalService;
import com.example.librarymanagementsystem.service.UserService;
import com.example.librarymanagementsystem.util.AuthHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// @Tag groups user management endpoints in Swagger UI
@Tag(name = "Users", description = "User profile and account management")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthHelper authHelper;
    private final RentalService rentalService;

    public UserController(UserService userService, AuthHelper authHelper, RentalService rentalService) {
        this.userService = userService;
        this.authHelper = authHelper;
        this.rentalService = rentalService;
    }

    @Operation(
            summary = "Get my profile",
            description = "Retrieve current user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        try {
            Optional<User> currentUser = authHelper.getCurrentUser(request);
            if (currentUser.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            User user = currentUser.get();
            return ResponseEntity.ok(Map.of(
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail(),
                            "role", user.getRole()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get profile: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Update my profile",
            description = "Update current user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "409", description = "Username or email already taken")
    })
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestBody Map<String, String> updateData, HttpServletRequest request) {
        try {
            Optional<String> userIdOpt = authHelper.getCurrentUserId(request);
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            String userId = userIdOpt.get();
            String username = updateData.get("username");
            String email = updateData.get("email");
            String password = updateData.get("password");

            // Basic validation
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Username is required"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Email is required"));
            }

            User updatedUser = userService.updateUser(userId, username, email, password);
            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "user", Map.of(
                            "id", updatedUser.getId(),
                            "username", updatedUser.getUsername(),
                            "email", updatedUser.getEmail(),
                            "role", updatedUser.getRole()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "List all users",
            description = "Retrieve all user accounts with pagination (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (max 100)")
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            // Validate pagination parameters
            authHelper.validatePagination(page, size);

            List<User> allUsers = userService.getAllUsers();
            List<User> pageContent = authHelper.applyPagination(allUsers, page, size);

            PagedResponse<User> response = new PagedResponse<>(pageContent, page, size, allUsers.size());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get users: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Get my rental history",
            description = "Retrieve current user's complete rental history with pagination"
    )
    @GetMapping("/me/rentals")
    public ResponseEntity<?> getMyRentalHistory(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (max 100)")
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            Optional<String> userIdOpt = authHelper.getCurrentUserId(request);
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            authHelper.validatePagination(page, size);

            String userId = userIdOpt.get();
            List<Rental> allRentals = rentalService.getUserRentalHistory(userId);
            List<Rental> pageContent = authHelper.applyPagination(allRentals, page, size);

            PagedResponse<Rental> response = new PagedResponse<>(pageContent, page, size, allRentals.size());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Update user",
            description = "Update any user's profile (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID to update")
            @PathVariable String id,
            @RequestBody Map<String, String> updateData,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            String username = updateData.get("username");
            String email = updateData.get("email");
            String password = updateData.get("password");

            User updatedUser = userService.updateUser(id, username, email, password);
            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "user", updatedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user account (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete user with active rentals"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID to delete")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Promote user to admin",
            description = "Grant admin privileges to user (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted successfully"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/promote")
    public ResponseEntity<?> promoteUser(
            @Parameter(description = "User ID to promote")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            User promotedUser = userService.promoteToAdmin(id);
            return ResponseEntity.ok(Map.of(
                    "message", "User promoted to admin successfully",
                    "user", promotedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Demote admin to user",
            description = "Remove admin privileges from user (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User demoted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot demote protected admin"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/demote")
    public ResponseEntity<?> demoteUser(
            @Parameter(description = "User ID to demote")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            User demotedUser = userService.demoteToUser(id);
            return ResponseEntity.ok(Map.of(
                    "message", "User demoted successfully",
                    "user", demotedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}