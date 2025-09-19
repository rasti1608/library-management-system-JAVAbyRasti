package com.example.librarymanagementsystem.model;

import com.example.librarymanagementsystem.model.enums.BookStatus;

public class Book {
    private String id;
    private String title;
    private String author;
    private String genre;
    private BookStatus status;

    // Default constructor
    public Book() {}

    // Constructor for new books
    public Book(String id, String title, String author, String genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.status = BookStatus.AVAILABLE;  // New books are available by default
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
}