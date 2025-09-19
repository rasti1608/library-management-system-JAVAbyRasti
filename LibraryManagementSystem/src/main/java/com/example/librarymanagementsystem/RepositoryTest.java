package com.example.librarymanagementsystem;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.repository.impl.JsonUserRepository;
import com.example.librarymanagementsystem.util.UuidGenerator;
import com.example.librarymanagementsystem.util.CacheHelper;

import java.util.List;

// Quick manual test for repository layer
public class RepositoryTest {

    public static void main(String[] args) {
        System.out.println("Testing Repository Layer...");

        // Create CacheHelper for repository
        CacheHelper cacheHelper = new CacheHelper();

        // Test UserRepository with CacheHelper
        JsonUserRepository userRepo = new JsonUserRepository(cacheHelper);

        // Load existing users (should have default admin)
        List<User> users = userRepo.findAll();
        System.out.println("Users loaded: " + users.size());

        // Test finding admin user
        userRepo.findByUsername("admin").ifPresent(admin -> {
            System.out.println("Found admin user: " + admin.getUsername());
            System.out.println("Admin email: " + admin.getEmail());
            System.out.println("Admin role: " + admin.getRole());
        });

        // Test creating new user
        User testUser = new User(
                UuidGenerator.generate(),
                "testuser",
                "hashedpassword",
                "test@test.com",
                UserRole.USER
        );

        userRepo.save(testUser);
        System.out.println("Test user saved");

        // Verify user was saved
        List<User> updatedUsers = userRepo.findAll();
        System.out.println("Users after save: " + updatedUsers.size());

        // Clean up - delete test user
        userRepo.delete(testUser.getId());
        System.out.println("Test user deleted");

        System.out.println("Repository test complete!");
    }
}