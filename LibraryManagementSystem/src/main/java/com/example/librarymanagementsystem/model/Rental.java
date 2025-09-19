package com.example.librarymanagementsystem.model;

import com.example.librarymanagementsystem.model.enums.RentalStatus;
import java.time.LocalDateTime;

public class Rental {
    private String id;
    private String userId;
    private String bookId;
    private LocalDateTime rentDate;
    private RentalStatus status;
    private LocalDateTime returnDate;

    // Default constructor
    public Rental() {}

    // Constructor for new rentals
    public Rental(String id, String userId, String bookId) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.rentDate = LocalDateTime.now();
        this.status = RentalStatus.ACTIVE;
        this.returnDate = null;  // Set when book is returned
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public LocalDateTime getRentDate() { return rentDate; }
    public void setRentDate(LocalDateTime rentDate) { this.rentDate = rentDate; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }

    public LocalDateTime getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDateTime returnDate) { this.returnDate = returnDate; }
}