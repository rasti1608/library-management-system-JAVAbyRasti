package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.dto.PagedResponse;
import com.example.librarymanagementsystem.service.BookService;
import com.example.librarymanagementsystem.service.RentalService;
import com.example.librarymanagementsystem.util.AuthHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// @Tag groups book-related endpoints in Swagger UI
@Tag(name = "Books", description = "Book catalog management and rental operations")
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final RentalService rentalService;
    private final AuthHelper authHelper;

    public BookController(BookService bookService, RentalService rentalService, AuthHelper authHelper) {
        this.bookService = bookService;
        this.rentalService = rentalService;
        this.authHelper = authHelper;
    }

    @Operation(
            summary = "Search and list books",
            description = "Browse available books with optional search filters and pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Search failed")
    })
    @GetMapping
    public ResponseEntity<?> searchBooks(
            @Parameter(description = "Search by book title (optional)")
            @RequestParam(required = false) String title,
            @Parameter(description = "Search by author name (optional)")
            @RequestParam(required = false) String author,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page (max 100)")
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        try {
            // Check if user is authenticated (required to browse books)
            if (!authHelper.isAuthenticated(request)) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            List<Book> books;

            // Search logic based on provided parameters
            if (title != null && !title.trim().isEmpty()) {
                books = bookService.searchByTitle(title);
            } else if (author != null && !author.trim().isEmpty()) {
                books = bookService.searchByAuthor(author);
            } else {
                books = bookService.getAllBooks();
            }

            // ADD THIS SECTION - Sort books consistently by ID to maintain catalog order
            books = books.stream()
                    .sorted(Comparator.comparing(Book::getId))
                    .collect(Collectors.toList());

            // Simple pagination
            int start = page * size;
            int end = Math.min(start + size, books.size());
            List<Book> pageContent = books.subList(Math.max(0, start), end);

            PagedResponse<Book> response = new PagedResponse<>(pageContent, page, size, books.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Add new book",
            description = "Add a new book to the library catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Map<String, String> bookData, HttpServletRequest request) {
        try {
            // Check if user is admin (only admins can add books)
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            String title = bookData.get("title");
            String author = bookData.get("author");
            String genre = bookData.get("genre");

            // Basic validation
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Title is required"));
            }
            if (author == null || author.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Author is required"));
            }

            Book newBook = bookService.addBook(title, author, genre);
            return ResponseEntity.ok(Map.of(
                    "message", "Book added successfully",
                    "book", newBook
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Rent a book",
            description = "Rent an available book for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book rented successfully"),
            @ApiResponse(responseCode = "400", description = "Book not available or rental limit exceeded"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PostMapping("/{id}/rent")
    public ResponseEntity<?> rentBook(
            @Parameter(description = "Book ID to rent")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            // Get current user ID from session
            Optional<String> userIdOpt = authHelper.getCurrentUserId(request);
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            String userId = userIdOpt.get();

            // Call service to handle rental business logic
            Rental rental = rentalService.rentBook(userId, id);

            return ResponseEntity.ok(Map.of(
                    "message", "Book rented successfully",
                    "rental", Map.of(
                            "id", rental.getId(),
                            "bookId", rental.getBookId(),
                            "rentDate", rental.getRentDate()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Return a book",
            description = "Return a previously rented book"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "400", description = "No active rental found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(
            @Parameter(description = "Book ID to return")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            // Get current user ID from session
            Optional<String> userIdOpt = authHelper.getCurrentUserId(request);
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            String userId = userIdOpt.get();

            // Find the rental record for this book and user
            List<Rental> userRentals = rentalService.getUserActiveRentals(userId);
            Optional<Rental> rentalToReturn = userRentals.stream()
                    .filter(rental -> rental.getBookId().equals(id))
                    .findFirst();

            if (rentalToReturn.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "No active rental found for this book"));
            }

            // Return the book
            Rental returnedRental = rentalService.returnBook(rentalToReturn.get().getId(), userId);

            return ResponseEntity.ok(Map.of(
                    "message", "Book returned successfully",
                    "rental", Map.of(
                            "id", returnedRental.getId(),
                            "returnDate", returnedRental.getReturnDate()
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Get my rentals",
            description = "Retrieve current user's active book rentals"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rentals retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/my-rentals")
    public ResponseEntity<?> getMyRentals(HttpServletRequest request) {
        try {
            // Get current user ID from session
            Optional<String> userIdOpt = authHelper.getCurrentUserId(request);
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
            }

            String userId = userIdOpt.get();
            List<Rental> activeRentals = rentalService.getUserActiveRentals(userId);

            return ResponseEntity.ok(Map.of(
                    "rentals", activeRentals,
                    "count", activeRentals.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}