package com.example.librarymanagementsystem.model;

import com.example.librarymanagementsystem.model.enums.UserRole;

public class User {
    private String id;
    private String username;
    private String passwordHash;
    private String email;
    private UserRole role;
    private boolean isProtected;  // Cannot delete if true
    private boolean mustChangePassword;

    // Default constructor
    public User() {}

    // Constructor for new users
    public User(String id, String username, String passwordHash, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.isProtected = false;
        this.mustChangePassword = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isProtected() { return isProtected; }
    public void setProtected(boolean isProtected) { this.isProtected = isProtected; }

    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
}