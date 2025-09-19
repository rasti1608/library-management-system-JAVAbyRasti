package com.example.librarymanagementsystem.repository.impl;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.util.JsonFileHandler;
import com.example.librarymanagementsystem.util.CacheHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Optional;

// @Repository tells Spring this is a data access component
@Repository
public class JsonUserRepository implements UserRepository {

    private final JsonFileHandler<User> fileHandler;

    // Constructor injection - Spring provides CacheHelper automatically
    // CacheHelper is needed for JsonFileHandler's 3-parameter constructor
    public JsonUserRepository(CacheHelper cacheHelper) {
        // Handle List<User> type for JSON operations
        this.fileHandler = new JsonFileHandler<>("data/users.json", new TypeReference<List<User>>() {}, cacheHelper);
    }

    @Override
    public List<User> findAll() {
        return fileHandler.readFromFile();
    }

    @Override
    public Optional<User> findById(String id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst();
    }

    @Override
    public User save(User user) {
        List<User> users = findAll();
        // Remove existing user with same ID if updating
        users.removeIf(existingUser -> existingUser.getId().equals(user.getId()));
        users.add(user);
        fileHandler.writeToFile(users);
        return user;
    }

    @Override
    public void delete(String id) {
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(id));
        fileHandler.writeToFile(users);
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