package com.example.librarymanagementsystem.service;

import com.example.librarymanagementsystem.model.Book;
import com.example.librarymanagementsystem.model.enums.BookStatus;
import com.example.librarymanagementsystem.repository.BookRepository;
import com.example.librarymanagementsystem.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RentalRepository rentalRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId("test-book-id");
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setGenre("Fiction");
        testBook.setStatus(BookStatus.AVAILABLE);
    }

    @Test
    void testGetAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void testFindById() {
        // Arrange
        when(bookRepository.findById("test-book-id")).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = bookService.findById("test-book-id");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }

    @Test
    void testSearchByTitle() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findByTitleContaining("Test")).thenReturn(books);

        // Act
        List<Book> result = bookService.searchByTitle("Test");

        // Assert
        assertEquals(1, result.size());
        verify(bookRepository).findByTitleContaining("Test");
    }

    @Test
    void testAddBookSuccess() {
        // Arrange
        when(bookRepository.existsByTitleAndAuthor("New Book", "New Author")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Book result = bookService.addBook("New Book", "New Author", "Fiction");

        // Assert
        assertNotNull(result);
        verify(bookRepository).existsByTitleAndAuthor("New Book", "New Author");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testAddBookFailureDuplicate() {
        // Arrange
        when(bookRepository.existsByTitleAndAuthor("Existing Book", "Existing Author")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.addBook("Existing Book", "Existing Author", "Fiction");
        });

        assertEquals("Book with same title and author already exists", exception.getMessage());
    }

    @Test
    void testDeleteBookSuccess() {
        // Arrange
        when(bookRepository.findById("test-book-id")).thenReturn(Optional.of(testBook));

        // Act
        bookService.deleteBook("test-book-id");

        // Assert
        verify(bookRepository).delete("test-book-id");
    }

    @Test
    void testDeleteBookFailureRented() {
        // Arrange
        testBook.setStatus(BookStatus.RENTED);
        when(bookRepository.findById("test-book-id")).thenReturn(Optional.of(testBook));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook("test-book-id");
        });

        assertEquals("Cannot delete rented book. Book must be returned first.", exception.getMessage());
    }

    @Test
    void testGetAvailableBooks() {
        // Arrange
        Book availableBook = new Book();
        availableBook.setStatus(BookStatus.AVAILABLE);
        Book rentedBook = new Book();
        rentedBook.setStatus(BookStatus.RENTED);

        when(bookRepository.findAll()).thenReturn(Arrays.asList(availableBook, rentedBook));

        // Act
        List<Book> result = bookService.getAvailableBooks();

        // Assert
        assertEquals(1, result.size());
        assertEquals(BookStatus.AVAILABLE, result.get(0).getStatus());
    }
}