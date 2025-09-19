package com.example.librarymanagementsystem.repository.impl;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.util.JsonFileHandler;
import com.example.librarymanagementsystem.util.CacheHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository marks this as data access layer component
@Repository
public class JsonBookRepository implements BookRepository {

    private final JsonFileHandler<Book> fileHandler;

    // Constructor injection with CacheHelper dependency
    public JsonBookRepository(CacheHelper cacheHelper) {
        // Create JsonFileHandler with caching support
        this.fileHandler = new JsonFileHandler<>("data/books.json", new TypeReference<List<Book>>() {}, cacheHelper);
    }

    @Override
    public List<Book> findAll() {
        return fileHandler.readFromFile();
    }

    @Override
    public Optional<Book> findById(String id) {
        return findAll().stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }

    @Override
    public Book save(Book book) {
        List<Book> books = findAll();
        // Remove existing book with same ID for updates
        books.removeIf(existingBook -> existingBook.getId().equals(book.getId()));
        books.add(book);
        fileHandler.writeToFile(books);
        return book;
    }

    @Override
    public void delete(String id) {
        List<Book> books = findAll();
        books.removeIf(book -> book.getId().equals(id));
        fileHandler.writeToFile(books);
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        // Case-insensitive search by title
        return findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase().trim()))
                .toList();
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        // Case-insensitive search by author
        return findAll().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase().trim()))
                .toList();
    }

    @Override
    public boolean existsByTitleAndAuthor(String title, String author) {
        // Check for duplicate title + author combination
        return findAll().stream()
                .anyMatch(book ->
                        book.getTitle().equalsIgnoreCase(title.trim()) &&
                                book.getAuthor().equalsIgnoreCase(author.trim())
                );
    }
}