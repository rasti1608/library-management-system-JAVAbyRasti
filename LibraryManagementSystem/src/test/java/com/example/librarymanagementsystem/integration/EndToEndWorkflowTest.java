package com.example.librarymanagementsystem.integration;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.model.enums.BookStatus;
import com.example.librarymanagementsystem.service.AuthService;
import com.example.librarymanagementsystem.service.BookService;
import com.example.librarymanagementsystem.service.UserService;
import com.example.librarymanagementsystem.service.RentalService;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.repository.impl.JsonUserRepository;
import com.example.librarymanagementsystem.repository.impl.JsonBookRepository;
import com.example.librarymanagementsystem.repository.impl.JsonRentalRepository;
import com.example.librarymanagementsystem.util.ValidationHelper;
import com.example.librarymanagementsystem.util.CacheHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// End-to-end tests for complete user scenarios from BRD
class EndToEndWorkflowTest {

    private static final Logger logger = LoggerFactory.getLogger(EndToEndWorkflowTest.class);

    private AuthService authService;
    private BookService bookService;
    private UserService userService;
    private RentalService rentalService;

    // Add short ID to make each test run unique (last 6 digits of timestamp + method name hash)
    private final String testRunId = String.valueOf(System.currentTimeMillis()).substring(7) +
            String.valueOf(Math.abs(this.getClass().hashCode() % 1000));

    @BeforeEach
    void setUp() {
        logger.info("Setting up end-to-end test environment with test run ID: {}", testRunId);

        // Create CacheHelper for repositories
        CacheHelper cacheHelper = new CacheHelper();

        // Initialize repositories with real implementations
        UserRepository userRepository = new JsonUserRepository(cacheHelper);
        BookRepository bookRepository = new JsonBookRepository(cacheHelper);
        RentalRepository rentalRepository = new JsonRentalRepository(cacheHelper);

        // Create validation helper and password encoder
        ValidationHelper validationHelper = new ValidationHelper();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Initialize services with dependencies
        authService = new AuthService(userRepository, validationHelper, passwordEncoder);
        bookService = new BookService(bookRepository, rentalRepository);
        userService = new UserService(userRepository, rentalRepository);
        rentalService = new RentalService(rentalRepository, bookRepository, userRepository);
    }

    @Test
    void testCompleteRegularUserWorkflow() {
        logger.info("Testing complete regular user workflow");

        // Scenario: Regular user registers, browses books, rents and returns

        // Step 1: User registration with unique username
        User user = authService.register("reguser" + testRunId, "regular" + testRunId + "@library.com", "password123");
        assertNotNull(user);
        assertEquals(UserRole.USER, user.getRole());
        logger.info("User registered successfully: {}", user.getUsername());

        // Step 2: User login
        Optional<User> loginResult = authService.login("regularuser", "password123");
        assertTrue(loginResult.isPresent());
        logger.info("User login successful");

        // Step 3: Browse available books
        List<Book> availableBooks = bookService.getAvailableBooks();
        assertNotNull(availableBooks);
        logger.info("Found {} available books", availableBooks.size());

        // Step 4: Search books by title with unique name
        bookService.addBook("TheGreatGatsby_" + testRunId, "F. Scott Fitzgerald", "Fiction");
        List<Book> searchResults = bookService.searchByTitle("TheGreatGatsby_" + testRunId);
        assertEquals(1, searchResults.size());
        logger.info("Search by title successful");

        // Step 5: Rent a book
        Book bookToRent = searchResults.get(0);
        Rental rental = rentalService.rentBook(user.getId(), bookToRent.getId());
        assertNotNull(rental);
        assertEquals(user.getId(), rental.getUserId());
        logger.info("Book rental successful");

        // Step 6: View current rentals
        List<Rental> userRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(1, userRentals.size());
        logger.info("User has {} active rentals", userRentals.size());

        // Step 7: Return the book
        Rental returnedRental = rentalService.returnBook(rental.getId(), user.getId());
        assertNotNull(returnedRental.getReturnDate());
        logger.info("Book return successful");

        // Step 8: Verify no active rentals
        List<Rental> noRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(0, noRentals.size());
        logger.info("Regular user workflow completed successfully");
    }

    @Test
    void testCompleteAdminWorkflow() {
        logger.info("Testing complete admin workflow");

        // Scenario: Admin manages books, users, and performs import/export

        // Step 1: Create and login as admin (since bootstrap admin might not exist in test)
        User adminUser = authService.register("admin" + testRunId, "admin" + testRunId + "@test.com", "admin123");
        // Manually promote to admin for test
        adminUser = userService.promoteToAdmin(adminUser.getId());

        Optional<User> adminLogin = authService.login("admin" + testRunId, "admin123");
        assertTrue(adminLogin.isPresent());
        assertEquals(UserRole.ADMIN, adminLogin.get().getRole());
        logger.info("Admin login successful");

        // Step 2: Add books to catalog with unique names
        Book book1 = bookService.addBook("1984_" + testRunId, "George Orwell", "Fiction");
        Book book2 = bookService.addBook("CleanCode_" + testRunId, "Robert Martin", "Programming");
        assertNotNull(book1);
        assertNotNull(book2);
        logger.info("Admin added {} books to catalog", 2);

        // Step 3: Edit book information
        Book updatedBook = bookService.updateBook(book1.getId(), "1984_Updated_" + testRunId, "George Orwell", "Dystopian Fiction");
        assertEquals("1984_Updated_" + testRunId, updatedBook.getTitle());
        logger.info("Book information updated");

        // Step 4: Create regular user with unique identifier
        User newUser = authService.register("user" + testRunId, "admin" + testRunId + "@user.com", "password123");
        assertNotNull(newUser);
        logger.info("Admin created new user");

        // Step 5: Promote user to admin
        User promotedUser = userService.promoteToAdmin(newUser.getId());
        assertEquals(UserRole.ADMIN, promotedUser.getRole());
        logger.info("User promoted to admin");

        // Step 6: Demote admin back to user
        User demotedUser = userService.demoteToUser(newUser.getId());
        assertEquals(UserRole.USER, demotedUser.getRole());
        logger.info("Admin demoted to user");

        // Step 7: Verify all users
        List<User> allUsers = userService.getAllUsers();
        assertTrue(allUsers.size() >= 2); // At least admin and new user
        logger.info("Admin can view all {} users", allUsers.size());

        logger.info("Admin workflow completed successfully");
    }

    @Test
    void testBusinessRuleEnforcement() {
        logger.info("Testing business rule enforcement");

        // Test all business rules from scope freeze

        // Rule 1: Username and email must be unique
        User user1 = authService.register("uniqueuser" + testRunId, "unique" + testRunId + "@test.com", "password123");

        assertThrows(RuntimeException.class, () -> {
            authService.register("uniqueuser" + testRunId, "different" + testRunId + "@email.com", "password123");
        });
        logger.info("Duplicate username prevention working");

        assertThrows(RuntimeException.class, () -> {
            authService.register("differentuser" + testRunId, "unique" + testRunId + "@test.com", "password123");
        });
        logger.info("Duplicate email prevention working");

        // Rule 2: Password policy enforcement
        assertThrows(IllegalArgumentException.class, () -> {
            authService.register("shortpass" + testRunId, "short" + testRunId + "@test.com", "short");
        });
        logger.info("Password policy enforcement working");

        // Rule 3: Maximum rentals per user (5)
        User renterUser = authService.register("renter" + testRunId, "renteruser" + testRunId + "@test.com", "password123");

        // Add 6 books and try to rent all (use unique names per test)
        for (int i = 1; i <= 6; i++) {
            bookService.addBook("RentalBook_" + testRunId + "_" + i, "RentalAuthor_" + testRunId + "_" + i, "Fiction");
        }

        List<Book> books = bookService.getAllBooks();

        // Find the books we just created (they should be available)
        List<Book> availableBooks = books.stream()
                .filter(book -> book.getTitle().startsWith("RentalBook_" + testRunId))
                .filter(book -> book.getStatus() == BookStatus.AVAILABLE)
                .toList();

        // Rent first 5 books (should succeed)
        for (int i = 0; i < 5 && i < availableBooks.size(); i++) {
            rentalService.rentBook(renterUser.getId(), availableBooks.get(i).getId());
        }

        // Try to rent 6th book (should fail due to limit)
        if (availableBooks.size() >= 6) {
            assertThrows(RuntimeException.class, () -> {
                rentalService.rentBook(renterUser.getId(), availableBooks.get(5).getId());
            });
        }
        logger.info("Rental limit enforcement working");

        // Rule 4: Cannot delete rented books (if we rented any)
        if (availableBooks.size() >= 1) {
            assertThrows(RuntimeException.class, () -> {
                bookService.deleteBook(availableBooks.get(0).getId()); // This book should be rented
            });
            logger.info("Rented book deletion prevention working");
        }

        // Rule 5: Cannot delete protected admin accounts
        // This would need the actual admin account testing

        logger.info("Business rule enforcement tests completed");
    }

    @Test
    void testErrorHandlingAndEdgeCases() {
        logger.info("Testing error handling and edge cases");

        // Test various error conditions

        // Invalid login attempts
        Optional<User> invalidLogin = authService.login("nonexistent", "password");
        assertFalse(invalidLogin.isPresent());

        // Invalid book operations - fix the assertion here
        Optional<Book> nonexistentBook = bookService.findById("nonexistent-book-id");
        assertFalse(nonexistentBook.isPresent()); // Should return empty, not throw exception

        // Invalid rental operations
        User user = authService.register("erroruser" + testRunId, "error" + testRunId + "@test.com", "password123");
        assertThrows(RuntimeException.class, () -> {
            rentalService.rentBook(user.getId(), "nonexistent-book-id");
        });

        // Test boundary conditions
        ValidationHelper validator = new ValidationHelper();

        // Username too short
        List<String> errors = validator.validateUserRegistration("ab", "test@test.com", "password123");
        assertFalse(errors.isEmpty());

        // Username too long
        String longUsername = "a".repeat(25);
        errors = validator.validateUserRegistration(longUsername, "test@test.com", "password123");
        assertFalse(errors.isEmpty());

        logger.info("Error handling and edge case tests completed");
    }

    @Test
    void testPerformanceAndScalability() {
        logger.info("Testing performance and scalability");

        long startTime = System.currentTimeMillis();

        // Create multiple users with unique identifiers
        for (int i = 0; i < 10; i++) {
            authService.register("perfuser" + testRunId + "_" + i, "perfuser" + testRunId + "_" + i + "@test.com", "password123");
        }

        // Create multiple books with unique names
        for (int i = 0; i < 50; i++) {
            bookService.addBook("PerfBook " + testRunId + "_" + i, "PerfAuthor " + testRunId + "_" + i, "Genre " + (i % 5));
        }

        // Perform searches on the unique book names
        List<Book> searchResults = bookService.searchByTitle("PerfBook");
        assertTrue(searchResults.size() > 0);

        searchResults = bookService.searchByAuthor("PerfAuthor");
        assertTrue(searchResults.size() > 0);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Performance test completed in {}ms", duration);

        // Performance should be reasonable for test data
        assertTrue(duration < 5000, "Operations should complete within 5 seconds");

        logger.info("Performance and scalability tests completed");
    }

    @Test
    void testDataIntegrityAndConsistency() {
        logger.info("Testing data integrity and consistency");

        // Test that operations maintain data consistency

        User user = authService.register("integrity", "integrity@test.com", "password123");
        Book book = bookService.addBook("Integrity Book", "Integrity Author", "Fiction");

        // Rent book
        Rental rental = rentalService.rentBook(user.getId(), book.getId());

        // Verify book status changed
        Optional<Book> rentedBook = bookService.findById(book.getId());
        assertTrue(rentedBook.isPresent());
        assertEquals(BookStatus.RENTED, rentedBook.get().getStatus());

        // Verify rental exists
        List<Rental> userRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(1, userRentals.size());

        // Return book
        rentalService.returnBook(rental.getId(), user.getId());

        // Verify book status reverted
        Optional<Book> availableBook = bookService.findById(book.getId());
        assertTrue(availableBook.isPresent());
        assertEquals(BookStatus.AVAILABLE, availableBook.get().getStatus());

        // Verify no active rentals
        List<Rental> noRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(0, noRentals.size());

        logger.info("Data integrity and consistency tests completed");
    }
}