package com.example.librarymanagementsystem.controller;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.dto.ImportSummary;
import com.example.librarymanagementsystem.service.BookService;
import com.example.librarymanagementsystem.util.AuthHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// @Tag groups admin-only endpoints in Swagger UI
@Tag(name = "Admin", description = "Administrative operations (Admin access required)")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BookService bookService;
    private final AuthHelper authHelper;
    private final ObjectMapper objectMapper;

    public AdminController(BookService bookService, AuthHelper authHelper) {
        this.bookService = bookService;
        this.authHelper = authHelper;
        this.objectMapper = new ObjectMapper();
    }

    @Operation(
            summary = "Export book catalog",
            description = "Download complete book catalog as JSON file with timestamp"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export successful - JSON file download"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "500", description = "Export failed")
    })
    @GetMapping("/export")
    public ResponseEntity<?> exportBooks(HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            List<Book> allBooks = bookService.getAllBooks();
            String jsonContent = objectMapper.writeValueAsString(allBooks);

            // Create timestamped filename
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String filename = "library_export_" + timestamp + ".json";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonContent);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Export failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Import books from file",
            description = "Upload JSON file to import books (append-only, skips duplicates)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import completed with summary"),
            @ApiResponse(responseCode = "400", description = "Invalid file or format"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "413", description = "File size exceeds 10MB limit"),
            @ApiResponse(responseCode = "422", description = "Invalid JSON format")
    })
    @PostMapping("/import")
    public ResponseEntity<?> importBooks(
            @Parameter(description = "JSON file containing book data (max 10MB)")
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "File is required"));
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(400).body(Map.of("error", "File size exceeds 10MB limit"));
            }

            if (!file.getContentType().equals("application/json")) {
                return ResponseEntity.status(400).body(Map.of("error", "File must be JSON format"));
            }

            // Parse JSON file
            List<Map<String, String>> importedBooks;
            try {
                String jsonContent = new String(file.getBytes());
                importedBooks = objectMapper.readValue(jsonContent, new TypeReference<List<Map<String, String>>>() {});
            } catch (Exception e) {
                return ResponseEntity.status(422).body(Map.of("error", "Invalid JSON format: " + e.getMessage()));
            }

            // Process imported books
            int added = 0;
            int skipped = 0;
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < importedBooks.size(); i++) {
                Map<String, String> bookData = importedBooks.get(i);

                try {
                    String title = bookData.get("title");
                    String author = bookData.get("author");
                    String genre = bookData.get("genre");

                    if (title == null || title.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Missing title field");
                        continue;
                    }
                    if (author == null || author.trim().isEmpty()) {
                        errors.add("Row " + (i + 1) + ": Missing author field");
                        continue;
                    }

                    bookService.addBook(title.trim(), author.trim(), genre != null ? genre.trim() : null);
                    added++;

                } catch (RuntimeException e) {
                    if (e.getMessage().contains("already exists")) {
                        skipped++;
                    } else {
                        errors.add("Row " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

            ImportSummary summary = new ImportSummary(added, skipped, errors);

            return ResponseEntity.ok(Map.of(
                    "message", "Import completed",
                    "summary", summary
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Import failed: " + e.getMessage()));
        }
    }

    @Operation(
            summary = "Add book",
            description = "Add new book to catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "409", description = "Book already exists")
    })
    @PostMapping("/books")
    public ResponseEntity<?> addBook(@RequestBody Map<String, String> bookData, HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            String title = bookData.get("title");
            String author = bookData.get("author");
            String genre = bookData.get("genre");

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Title is required"));
            }
            if (author == null || author.trim().isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Author is required"));
            }

            Book newBook = bookService.addBook(title, author, genre);
            return ResponseEntity.status(201).body(Map.of(
                    "message", "Book added successfully",
                    "book", newBook
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Update book",
            description = "Update existing book information (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(
            @Parameter(description = "Book ID to update")
            @PathVariable String id,
            @RequestBody Map<String, String> bookData,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            String title = bookData.get("title");
            String author = bookData.get("author");
            String genre = bookData.get("genre");

            Book updatedBook = bookService.updateBook(id, title, author, genre);
            return ResponseEntity.ok(Map.of(
                    "message", "Book updated successfully",
                    "book", updatedBook
            ));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Delete book",
            description = "Remove book from catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete rented book"),
            @ApiResponse(responseCode = "403", description = "Admin access required"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "Book ID to delete")
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            if (!authHelper.isAdmin(request)) {
                return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
            }

            bookService.deleteBook(id);
            return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}