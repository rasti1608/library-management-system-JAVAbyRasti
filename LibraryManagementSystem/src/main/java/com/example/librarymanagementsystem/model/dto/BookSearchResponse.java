package com.example.librarymanagementsystem.model.dto;

import com.example.librarymanagementsystem.model.Book;

public class BookSearchResponse {
    private String id;
    private String title;
    private String author;
    private String genre;
    private String status;

    // Default constructor
    public BookSearchResponse() {}

    // Constructor from Book entity
    public BookSearchResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.genre = book.getGenre();
        this.status = book.getStatus().toString();
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}