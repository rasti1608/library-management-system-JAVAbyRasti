package com.example.librarymanagementsystem.repository;

import com.example.librarymanagementsystem.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    void delete(String id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}