package com.example.librarymanagementsystem.repository;

import com.example.librarymanagementsystem.model.Rental;
import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    List<Rental> findAll();
    Optional<Rental> findById(String id);
    Rental save(Rental rental);
    void delete(String id);
    List<Rental> findByUserId(String userId);
    List<Rental> findByBookId(String bookId);
    List<Rental> findActiveRentals();
}