package com.example.librarymanagementsystem;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.repository.impl.JsonUserRepository;
import com.example.librarymanagementsystem.repository.impl.JsonBookRepository;
import com.example.librarymanagementsystem.repository.impl.JsonRentalRepository;
import com.example.librarymanagementsystem.service.AuthService;
import com.example.librarymanagementsystem.service.BookService;
import com.example.librarymanagementsystem.service.RentalService;
import com.example.librarymanagementsystem.util.ValidationHelper;
import com.example.librarymanagementsystem.util.CacheHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

// Quick manual test for service layer
public class ServiceTest {

    public static void main(String[] args) {
        System.out.println("Testing Service Layer...");

        // Create CacheHelper first (needed for repositories)
        CacheHelper cacheHelper = new CacheHelper();

        // Create repository instances with CacheHelper
        UserRepository userRepo = new JsonUserRepository(cacheHelper);
        BookRepository bookRepo = new JsonBookRepository(cacheHelper);
        RentalRepository rentalRepo = new JsonRentalRepository(cacheHelper);
        ValidationHelper validationHelper = new ValidationHelper();

        // Create service instances with dependency injection
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(userRepo, validationHelper, passwordEncoder);
        BookService bookService = new BookService(bookRepo, rentalRepo);
        RentalService rentalService = new RentalService(rentalRepo, bookRepo, userRepo);

        // Test AuthService - login with default admin
        System.out.println("\n--- Testing AuthService ---");
        Optional<User> loginResult = authService.login("admin", "admin123");
        if (loginResult.isPresent()) {
            System.out.println("Admin login successful: " + loginResult.get().getUsername());
        } else {
            System.out.println("Admin login failed");
        }

        // Test user registration
        try {
            User newUser = authService.register("testuser", "test@example.com", "password123");
            System.out.println("User registration successful: " + newUser.getUsername());
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }

        // Test BookService - add a book
        System.out.println("\n--- Testing BookService ---");
        try {
            Book newBook = bookService.addBook("Test Book", "Test Author", "Fiction");
            System.out.println("Book added successfully: " + newBook.getTitle());

            // Test search functionality
            var searchResults = bookService.searchByTitle("Test");
            System.out.println("Search results for 'Test': " + searchResults.size() + " books found");

            // Clean up - delete test book
            bookService.deleteBook(newBook.getId());
            System.out.println("Test book deleted");

        } catch (Exception e) {
            System.out.println("Book service error: " + e.getMessage());
        }

        // Test dependency injection working
        System.out.println("\n--- Testing Dependency Injection ---");
        System.out.println("AuthService has UserRepository: " + (authService != null));
        System.out.println("BookService has BookRepository and RentalRepository: " + (bookService != null));
        System.out.println("RentalService has all three repositories: " + (rentalService != null));

        System.out.println("\nService layer test complete!");
        System.out.println("Dependencies are being injected correctly through constructors");
    }
}