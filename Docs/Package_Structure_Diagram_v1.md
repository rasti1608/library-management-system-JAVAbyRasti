# Library Management System — Package Structure (v1.0)

```
com.example.library
├── LibraryApplication.java
├── config
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller
│   ├── AuthController.java
│   ├── BookController.java
│   ├── UserController.java
│   └── AdminController.java
├── model
│   ├── Book.java
│   ├── User.java
│   ├── Rental.java
│   └── enums
│       ├── BookStatus.java         # AVAILABLE | RENTED
│       ├── RentalStatus.java       # ACTIVE | CLOSED
│       └── UserRole.java           # USER | ADMIN
├── model
│   └── dto
│       ├── LoginRequest.java
│       ├── RegisterRequest.java    # optional (can be folded into LoginRequest if preferred)
│       ├── PagedResponse.java      # pagination envelope
│       ├── BookSearchResponse.java # search result DTO (optional)
│       └── ImportSummary.java      # {added, skipped, errors[]}
├── repository
│   ├── BookRepository.java         # interface
│   ├── UserRepository.java         # interface
│   └── RentalRepository.java       # interface
├── repository
│   └── impl
│       ├── JsonBookRepository.java
│       ├── JsonUserRepository.java
│       └── JsonRentalRepository.java
├── service
│   ├── AuthService.java
│   ├── BookService.java
│   ├── UserService.java
│   └── RentalService.java
└── util
    ├── JsonFileHandler.java        # atomic write: temp → rename
    └── UuidGenerator.java
```

**Legend**
- `config` — Spring Security, Swagger/OpenAPI config
- `controller` — REST endpoints (HTTP layer only; delegate to services)
- `service` — business rules (rent/return, uniqueness checks, import summary)
- `repository` — data-access interfaces; `repository.impl` — JSON-backed implementations
- `model` — domain entities and enums
- `model/dto` — request/response payload types (API contracts)
- `util` — helpers (file I/O, IDs)

> Tip: keep everything under `com.example.library` so `@SpringBootApplication` component scan picks it up.
