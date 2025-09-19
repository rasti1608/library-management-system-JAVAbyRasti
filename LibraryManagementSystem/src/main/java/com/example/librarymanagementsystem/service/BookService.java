package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.enums.BookStatus;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.util.UuidGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service marks this as business logic component for Spring
@Service
public class BookService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;

    // Constructor injection - Spring provides both repository implementations
    // Demonstrates dependency injection with multiple dependencies
    public BookService(BookRepository bookRepository, RentalRepository rentalRepository) {
        this.bookRepository = bookRepository;
        this.rentalRepository = rentalRepository;
    }

    // Get all books with pagination support
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Find book by ID
    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    // Search books by title (case-insensitive)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    // Search books by author (case-insensitive)
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    // Add new book (admin only)
    public Book addBook(String title, String author, String genre) {
        // Business rule: check for duplicate title + author combination
        if (bookRepository.existsByTitleAndAuthor(title, author)) {
            throw new RuntimeException("Book with same title and author already exists");
        }

        Book newBook = new Book(
                UuidGenerator.generate(),
                title.trim(),
                author.trim(),
                genre != null ? genre.trim() : null
        );

        return bookRepository.save(newBook);
    }

    // Update existing book (admin only)
    public Book updateBook(String id, String title, String author, String genre) {
        Optional<Book> existingBook = bookRepository.findById(id);
        if (existingBook.isEmpty()) {
            throw new RuntimeException("Book not found");
        }

        Book book = existingBook.get();
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setGenre(genre != null ? genre.trim() : null);

        return bookRepository.save(book);
    }

    // Delete book (admin only)
    public void deleteBook(String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new RuntimeException("Book not found");
        }

        // Business rule: cannot delete rented books
        if (book.get().getStatus() == BookStatus.RENTED) {
            throw new RuntimeException("Cannot delete rented book. Book must be returned first.");
        }

        bookRepository.delete(id);
    }

    // Get only available books for rental
    public List<Book> getAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(book -> book.getStatus() == BookStatus.AVAILABLE)
                .toList();
    }
}