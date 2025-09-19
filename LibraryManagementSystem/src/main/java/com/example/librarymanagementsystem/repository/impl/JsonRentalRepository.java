package com.example.librarymanagementsystem.repository.impl;

import com.example.librarymanagementsystem.model.Rental;
import com.example.librarymanagementsystem.model.enums.RentalStatus;
import com.example.librarymanagementsystem.repository.RentalRepository;
import com.example.librarymanagementsystem.util.JsonFileHandler;
import com.example.librarymanagementsystem.util.CacheHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// @Repository tells Spring this handles rental data access
@Repository
public class JsonRentalRepository implements RentalRepository {

    private final JsonFileHandler<Rental> fileHandler;

    // Constructor injection - Spring provides CacheHelper instance
    public JsonRentalRepository(CacheHelper cacheHelper) {
        // Handle List<Rental> type for JSON operations with caching
        this.fileHandler = new JsonFileHandler<>("data/rentals.json", new TypeReference<List<Rental>>() {}, cacheHelper);
    }

    @Override
    public List<Rental> findAll() {
        return fileHandler.readFromFile();
    }

    @Override
    public Optional<Rental> findById(String id) {
        return findAll().stream()
                .filter(rental -> rental.getId().equals(id))
                .findFirst();
    }

    @Override
    public Rental save(Rental rental) {
        List<Rental> rentals = findAll();
        // Remove existing rental with same ID for updates
        rentals.removeIf(existingRental -> existingRental.getId().equals(rental.getId()));
        rentals.add(rental);
        fileHandler.writeToFile(rentals);
        return rental;
    }

    @Override
    public void delete(String id) {
        List<Rental> rentals = findAll();
        rentals.removeIf(rental -> rental.getId().equals(id));
        fileHandler.writeToFile(rentals);
    }

    @Override
    public List<Rental> findByUserId(String userId) {
        // Find all rentals for specific user
        return findAll().stream()
                .filter(rental -> rental.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Rental> findByBookId(String bookId) {
        // Find all rentals for specific book
        return findAll().stream()
                .filter(rental -> rental.getBookId().equals(bookId))
                .toList();
    }

    @Override
    public List<Rental> findActiveRentals() {
        // Filter only active rentals (not returned yet)
        return findAll().stream()
                .filter(rental -> rental.getStatus() == RentalStatus.ACTIVE)
                .toList();
    }
}