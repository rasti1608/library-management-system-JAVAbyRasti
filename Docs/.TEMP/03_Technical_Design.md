# 📚 Library Management System – Technical Design

## Architecture
- **Framework**: Spring Boot 3.x  
- **Java**: 17+  
- **Build Tool**: Maven  
- **Storage**: JSON files  
- **Auth**: Session-based with BCrypt  
- **Docs**: Swagger/OpenAPI  
- **Tests**: JUnit 5 + Mockito【98†source】.

## Package Structure
```
com.library.management/
 ├── LibraryApplication.java (@SpringBootApplication)
 ├── config/ (SecurityConfig, SwaggerConfig)
 ├── controller/ (AuthController, BookController, UserController, AdminController)
 ├── service/ (AuthService, BookService, UserService, RentalService)
 ├── repository/ (interfaces + JSON impl)
 ├── model/ (User, Book, Rental, DTOs)
 └── util/ (JsonFileHandler, UuidGenerator)
```

## Core Classes
- **User**: UUID, username, passwordHash, email, role, protected flag.  
- **Book**: UUID, title, author, genre, status.  
- **Rental**: UUID, userId, bookId, rentDate, returnDate, status.

## Repository Pattern
Example: `BookRepository` interface with CRUD + search methods.  
Implemented via `JsonBookRepository` using `JsonFileHandler`.

## Storage
- Files in `/data` → `users.json`, `books.json`, `rentals.json`.  
- Atomic writes (temp → rename).  
- Single writer lock for thread safety.

## Authentication
- Login: validate user with BCrypt.  
- Session created on login, cleared on logout.  
- Role-based access enforced in controllers + services.

## API Design
- REST endpoints for auth, books, users, admin.  
- Pagination standardized with `PagedResponse<T>`.  
- Error format: `{ "error": "message", "details": [] }`.

## Security
- BCrypt (strength 10).  
- Min 8-char password with letter + number.  
- Default admin forced password change.  

## Business Logic
- Rental rules: availability, 5 rentals per user, only renter/admin returns.  
- Import/export: append-only, skip duplicates, summary returned.

## Testing
- Unit tests for services.  
- Mock repos for isolation.  
- Controller validation + auth tests.

## Config
- `application.properties`: server, session, paths, security, logging, Swagger【98†source】.

---
✅ Design ensures maintainability, clean separation of concerns, and secure operations.
