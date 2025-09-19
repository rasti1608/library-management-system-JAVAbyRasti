package com.example.librarymanagementsystem.integration;

import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.enums.UserRole;
import com.example.librarymanagementsystem.model.enums.BookStatus;
import com.example.librarymanagementsystem.service.AuthService;
import com.example.librarymanagementsystem.service.BookService;
import com.example.librarymanagementsystem.service.RentalService;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.repository.impl.JsonUserRepository;
import com.example.librarymanagementsystem.repository.impl.JsonBookRepository;
import com.example.librarymanagementsystem.repository.impl.JsonRentalRepository;
import com.example.librarymanagementsystem.util.CacheHelper;
import com.example.librarymanagementsystem.util.ValidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// Integration tests verify complete workflows across all layers
class LibraryIntegrationTest {

    private AuthService authService;
    private BookService bookService;
    private RentalService rentalService;

    private UserRepository userRepository;
    private BookRepository bookRepository;
    private RentalRepository rentalRepository;

    // @TempDir creates temporary directory for test files
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        // Create temporary JSON files for testing
        createTestDataFiles();

        // Create CacheHelper for repositories
        CacheHelper cacheHelper = new CacheHelper();

        // Initialize repositories with CacheHelper and test data directory
        userRepository = new JsonUserRepository(cacheHelper);
        bookRepository = new JsonBookRepository(cacheHelper);
        rentalRepository = new JsonRentalRepository(cacheHelper);

        // Initialize services with real dependencies
        ValidationHelper validationHelper = new ValidationHelper();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, validationHelper, passwordEncoder);
        bookService = new BookService(bookRepository, rentalRepository);
        rentalService = new RentalService(rentalRepository, bookRepository, userRepository);
    }

    private void createTestDataFiles() throws Exception {
        // Create empty JSON files for testing
        File usersFile = tempDir.resolve("users.json").toFile();
        File booksFile = tempDir.resolve("books.json").toFile();
        File rentalsFile = tempDir.resolve("rentals.json").toFile();

        try (FileWriter writer = new FileWriter(usersFile)) {
            writer.write("[]");
        }
        try (FileWriter writer = new FileWriter(booksFile)) {
            writer.write("[]");
        }
        try (FileWriter writer = new FileWriter(rentalsFile)) {
            writer.write("[]");
        }
    }

    @Test
    void testCompleteUserRegistrationAndLoginWorkflow() {
        // Test complete user workflow: register -> login -> validate

        // Register new user
        User registeredUser = authService.register("integrationuser", "integration@test.com", "password123");

        assertNotNull(registeredUser);
        assertEquals("integrationuser", registeredUser.getUsername());
        assertEquals(UserRole.USER, registeredUser.getRole());

        // Login with registered user
        Optional<User> loginResult = authService.login("integrationuser", "password123");

        assertTrue(loginResult.isPresent());
        assertEquals("integrationuser", loginResult.get().getUsername());
    }

    @Test
    void testCompleteBookManagementWorkflow() {
        // Test complete book workflow: add -> search -> update -> delete

        // Add new book
        Book addedBook = bookService.addBook("Integration Test Book", "Test Author", "Fiction");

        assertNotNull(addedBook);
        assertEquals("Integration Test Book", addedBook.getTitle());
        assertEquals(BookStatus.AVAILABLE, addedBook.getStatus());

        // Search by title
        List<Book> titleResults = bookService.searchByTitle("Integration");
        assertEquals(1, titleResults.size());
        assertEquals("Integration Test Book", titleResults.get(0).getTitle());

        // Search by author
        List<Book> authorResults = bookService.searchByAuthor("Test Author");
        assertEquals(1, authorResults.size());

        // Update book
        Book updatedBook = bookService.updateBook(addedBook.getId(), "Updated Title", "Updated Author", "Updated Genre");
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated Author", updatedBook.getAuthor());

        // Delete book
        bookService.deleteBook(addedBook.getId());

        // Verify deletion
        Optional<Book> deletedBook = bookService.findById(addedBook.getId());
        assertFalse(deletedBook.isPresent());
    }

    @Test
    void testCompleteRentalWorkflow() {
        // Test complete rental workflow: register user -> add book -> rent -> return

        // Step 1: Register user
        User user = authService.register("renteruser", "renter@test.com", "password123");

        // Step 2: Add book
        Book book = bookService.addBook("Rental Test Book", "Rental Author", "Fiction");

        // Step 3: Rent book
        Rental rental = rentalService.rentBook(user.getId(), book.getId());

        assertNotNull(rental);
        assertEquals(user.getId(), rental.getUserId());
        assertEquals(book.getId(), rental.getBookId());

        // Verify book status changed to rented
        Optional<Book> rentedBook = bookService.findById(book.getId());
        assertTrue(rentedBook.isPresent());
        assertEquals(BookStatus.RENTED, rentedBook.get().getStatus());

        // Verify user has active rental
        List<Rental> activeRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(1, activeRentals.size());

        // Step 4: Return book
        Rental returnedRental = rentalService.returnBook(rental.getId(), user.getId());

        assertNotNull(returnedRental.getReturnDate());

        // Verify book status changed back to available
        Optional<Book> availableBook = bookService.findById(book.getId());
        assertTrue(availableBook.isPresent());
        assertEquals(BookStatus.AVAILABLE, availableBook.get().getStatus());

        // Verify user has no active rentals
        List<Rental> noActiveRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(0, noActiveRentals.size());
    }

    @Test
    void testBusinessRuleViolations() {
        // Test various business rule violations

        // Register user
        User user = authService.register("businessuser", "business@test.com", "password123");

        // Add book
        Book book = bookService.addBook("Business Rule Book", "Rule Author", "Fiction");

        // Test: Cannot rent already rented book
        rentalService.rentBook(user.getId(), book.getId()); // First rental

        assertThrows(RuntimeException.class, () -> {
            rentalService.rentBook(user.getId(), book.getId()); // Try to rent again
        });

        // Test: Cannot delete rented book
        assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook(book.getId());
        });

        // Test: Cannot register duplicate username
        assertThrows(RuntimeException.class, () -> {
            authService.register("businessuser", "different@email.com", "password123");
        });

        // Test: Cannot register duplicate email
        assertThrows(RuntimeException.class, () -> {
            authService.register("differentuser", "business@test.com", "password123");
        });
    }

    @Test
    void testRentalLimitEnforcement() {
        // Test the 5-rental limit per user

        // Register user
        User user = authService.register("limituser", "limit@test.com", "password123");

        // Add 6 books
        Book book1 = bookService.addBook("Book 1", "Author 1", "Fiction");
        Book book2 = bookService.addBook("Book 2", "Author 2", "Fiction");
        Book book3 = bookService.addBook("Book 3", "Author 3", "Fiction");
        Book book4 = bookService.addBook("Book 4", "Author 4", "Fiction");
        Book book5 = bookService.addBook("Book 5", "Author 5", "Fiction");
        Book book6 = bookService.addBook("Book 6", "Author 6", "Fiction");

        // Rent 5 books (should succeed)
        rentalService.rentBook(user.getId(), book1.getId());
        rentalService.rentBook(user.getId(), book2.getId());
        rentalService.rentBook(user.getId(), book3.getId());
        rentalService.rentBook(user.getId(), book4.getId());
        rentalService.rentBook(user.getId(), book5.getId());

        // Try to rent 6th book (should fail)
        assertThrows(RuntimeException.class, () -> {
            rentalService.rentBook(user.getId(), book6.getId());
        });

        // Verify user has exactly 5 active rentals
        List<Rental> activeRentals = rentalService.getUserActiveRentals(user.getId());
        assertEquals(5, activeRentals.size());
    }

    @Test
    void testDataPersistenceAcrossOperations() {
        // Test that data persists correctly across multiple operations

        // Add initial data
        User user1 = authService.register("persist1", "persist1@test.com", "password123");
        User user2 = authService.register("persist2", "persist2@test.com", "password123");
        Book book1 = bookService.addBook("Persist Book 1", "Persist Author 1", "Fiction");
        Book book2 = bookService.addBook("Persist Book 2", "Persist Author 2", "Non-Fiction");

        // Verify all data exists
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(2, allBooks.size());

        // Create rental
        Rental rental = rentalService.rentBook(user1.getId(), book1.getId());

        // Verify rental exists
        List<Rental> allRentals = rentalService.getAllActiveRentals();
        assertEquals(1, allRentals.size());

        // Verify data integrity across services
        Optional<User> retrievedUser = userRepository.findById(user1.getId());
        assertTrue(retrievedUser.isPresent());

        Optional<Book> retrievedBook = bookService.findById(book1.getId());
        assertTrue(retrievedBook.isPresent());
        assertEquals(BookStatus.RENTED, retrievedBook.get().getStatus());
    }
}