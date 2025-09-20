package com.example.librarymanagementsystem.model;

import com.example.librarymanagementsystem.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    // --- core fields ---
// put these annotations directly on the fields
    @JsonAlias({"id", "userId"})
    private String id;

    private String username;
    private String passwordHash;

    @JsonAlias({"email", "userEmail"})
    private String email;

    private UserRole role;

    @JsonProperty("protected")
    private boolean isProtected;

    @JsonProperty("mustChangePassword")
    private boolean mustChangePassword;
// keep your no-args ctor, getters, and setters (WITHOUT extra annotations) as-is:
// public String getId() { return id; }  etc.
// public void setId(String id) { this.id = id; }
// public void setEmail(String email) { this.email = email; }

    // --- constructors ---
    public User() { }

    // --- getters ---
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isProtected() { return isProtected; }
    public boolean isMustChangePassword() { return mustChangePassword; }

    // --- setters (force Jackson binding where needed) ---
    @JsonProperty("id")
    public void setId(String id) { this.id = id; }

    public void setUsername(String username) { this.username = username; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @JsonProperty("email")
    public void setEmail(String email) { this.email = email; }

    public void setRole(UserRole role) { this.role = role; }

    public void setProtected(boolean aProtected) { this.isProtected = aProtected; }

    @JsonProperty("mustChangePassword")
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
