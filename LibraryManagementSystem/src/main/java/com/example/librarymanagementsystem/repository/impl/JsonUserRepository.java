package com.example.librarymanagementsystem.repository.impl;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.util.JsonFileHandler;
import com.example.librarymanagementsystem.util.CacheHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class JsonUserRepository implements UserRepository {

    private final JsonFileHandler<User> fileHandler;
    private List<User> users; // In-memory storage

    public JsonUserRepository(CacheHelper cacheHelper) {
        this.fileHandler = new JsonFileHandler<>("data/users.json", new TypeReference<List<User>>() {}, cacheHelper);
    }

    @PostConstruct
    public void loadInitialData() {
        try {
            System.out.println("Attempting to load user data...");
            List<User> initialUsers = fileHandler.readFromFile();
            this.users = new CopyOnWriteArrayList<>(initialUsers);
            System.out.println("Loaded " + users.size() + " users successfully");

            // Debug the first user's raw data
            this.users = new CopyOnWriteArrayList<>(initialUsers);
            System.out.println("Loaded " + users.size() + " users successfully");
            System.out.println("Users empty? " + users.isEmpty());

            if (!users.isEmpty()) {
                User firstUser = users.get(0);
                System.out.println("First user debug:");
                System.out.println("  ID: '" + firstUser.getId() + "'");
                System.out.println("  Username: '" + firstUser.getUsername() + "'");
                System.out.println("  Email: '" + firstUser.getEmail() + "'");
                System.out.println("  Role: '" + firstUser.getRole() + "'");
            }

            System.out.println("Loaded " + users.size() + " users successfully");
            if (users.isEmpty()) {
                System.out.println("WARNING: No users loaded! This will break authentication.");
            } else {
                User firstUser = users.get(0);
                System.out.println("First user debug:");
                // ... debug output
            }
            
        } catch (Exception e) {
            System.out.println("Failed to load user data: " + e.getMessage());
            e.printStackTrace();
            this.users = new CopyOnWriteArrayList<>();
        }
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users); // Return copy to prevent external modification
    }

    @Override
    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public User save(User user) {
        // Remove existing user with same ID if updating
        users.removeIf(existingUser -> existingUser.getId().equals(user.getId()));
        users.add(user);
        return user;
    }

    @Override
    public void delete(String id) {
        users.removeIf(user -> user.getId().equals(id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}