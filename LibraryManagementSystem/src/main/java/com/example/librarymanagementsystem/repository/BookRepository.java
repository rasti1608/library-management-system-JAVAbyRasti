package com.example.librarymanagementsystem.repository;

import com.example.librarymanagementsystem.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findById(String id);
    Book save(Book book);
    void delete(String id);
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    boolean existsByTitleAndAuthor(String title, String author);
}