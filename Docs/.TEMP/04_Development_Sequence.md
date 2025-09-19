# 📚 Library Management System – Development Sequence & Schedule

## Phase 1: Foundation (30m)
- Initialize Spring Boot project with Maven + Java 17【99†source】.  
- Create package structure.  
- Add dependencies (Spring Web, Security, OpenAPI).

## Phase 2: Data Models (45m)
- Create enums: UserRole, BookStatus, RentalStatus.  
- Create entities: User, Book, Rental.  
- Create DTOs: LoginRequest, RegisterRequest, PagedResponse, BookSearchResponse, ImportSummary.

## Phase 3: File I/O (60m)
- Utility classes: `JsonFileHandler`, `UuidGenerator`.  
- Create `data/` folder with users.json, books.json, rentals.json.  
- Bootstrap default admin in `users.json`.

## Phase 4: Repository Layer (90m)
- Interfaces: UserRepository, BookRepository, RentalRepository.  
- Implement JSON repos: CRUD, search, filtering.  
- Manual test with `main()`.

## Phase 5: Services (2h)
- AuthService (login/register).  
- BookService (CRUD + search).  
- UserService, RentalService.  
- Unit tests with Mockito.

## Phase 6: Controllers (90m)
- AuthController → register/login/logout.  
- BookController → search, rent/return.  
- Basic endpoint testing.

## Phase 7: Security (2h)
- Add session management.  
- Role-based access via AuthHelper.  
- Secure all endpoints.

## Phase 8: Admin Features (90m)
- AdminController → import/export.  
- UserController → manage users.  
- Implement import/export logic.

## Phase 9: Error Handling (60m)
- Global exception handler.  
- Validation annotations + custom validators.  
- Standard error response.

## Phase 10: Docs & Tests (90m)
- Swagger config + endpoint docs.  
- Unit + integration tests.  
- Document workflows.

## Phase 11: Polish (60m)
- Add pagination everywhere.  
- Optimize file I/O, caching, logging.  
- Final testing vs BRD & Scope Freeze.

---
✅ Suggested timeline: 2–3 days, 15–20 hours. Each phase builds directly on the last【99†source】.
