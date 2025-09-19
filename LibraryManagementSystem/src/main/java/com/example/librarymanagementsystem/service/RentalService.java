package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.User;
import com.example.librarymanagementsystem.model.enums.BookStatus;
import com.example.librarymanagementsystem.model.enums.RentalStatus;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.repository.UserRepository;
import com.example.librarymanagementsystem.util.UuidGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// @Service for rental business logic
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Maximum rentals per user from business rules
    private static final int MAX_RENTALS_PER_USER = 5;

    // Constructor injection with three repository dependencies
    public RentalService(RentalRepository rentalRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Rent a book to a user
    public Rental rentBook(String userId, String bookId) {
        // Validate user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Validate book exists
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("Book not found");
        }

        Book book = bookOpt.get();

        // Business rule: book must be available
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new RuntimeException("Book is not available for rental");
        }

        // Business rule: check rental limit per user
        List<Rental> userActiveRentals = rentalRepository.findByUserId(userId).stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .toList();

        if (userActiveRentals.size() >= MAX_RENTALS_PER_USER) {
            throw new RuntimeException("User has reached maximum rental limit of " + MAX_RENTALS_PER_USER + " books");
        }

        // Create rental record
        Rental rental = new Rental(
                UuidGenerator.generate(),
                userId,
                bookId
        );

        // Update book status to rented
        book.setStatus(BookStatus.RENTED);
        bookRepository.save(book);

        // Save rental record
        return rentalRepository.save(rental);
    }

    // Return a book
    public Rental returnBook(String rentalId, String userId) {
        Optional<Rental> rentalOpt = rentalRepository.findById(rentalId);
        if (rentalOpt.isEmpty()) {
            throw new RuntimeException("Rental not found");
        }

        Rental rental = rentalOpt.get();

        // Business rule: only the renter or admin can return books
        if (!rental.getUserId().equals(userId)) {
            // Check if user is admin (this would need admin check logic)
            throw new RuntimeException("Only the renter can return this book");
        }

        // Business rule: rental must be active
        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new RuntimeException("Rental is not active");
        }

        // Update rental status
        rental.setStatus(RentalStatus.CLOSED);
        rental.setReturnDate(LocalDateTime.now());

        // Update book status back to available
        Optional<Book> bookOpt = bookRepository.findById(rental.getBookId());
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
        }

        return rentalRepository.save(rental);
    }

    // Get user's active rentals
    public List<Rental> getUserActiveRentals(String userId) {
        return rentalRepository.findByUserId(userId).stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .toList();
    }

    // Get all active rentals (admin view)
    public List<Rental> getAllActiveRentals() {
        return rentalRepository.findActiveRentals();
    }

    // Get rental history for a user
    public List<Rental> getUserRentalHistory(String userId) {
        return rentalRepository.findByUserId(userId);
    }
}