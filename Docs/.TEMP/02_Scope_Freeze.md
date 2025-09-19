# 📚 Library Management System – Scope Freeze v1.0

## Authentication & User Management
- **Registration**: username, email, password (unique).  
- **Password policy**: 8+ characters, must contain letter + number.  
- **Bootstrap Admin**: admin/admin123 (protected, must change password).  
- **User Self-Management**: edit/view profile, see rentals.  
- **Admin Management**: view/edit/delete users, promote/demote (except protected)【97†source】.

## Book Management
- **Book structure**: title, author, genre, status (AVAILABLE/RENTED).  
- **User actions**: browse, search, rent/return.  
- **Admin actions**: add/edit/delete books (cannot delete RENTED).

## Import/Export
- Export full catalog → JSON file (timestamped).  
- Import → append-only, skip duplicates, show summary.  

## Data Storage
- JSON files: `users.json`, `books.json`, `rentals.json` with UUID IDs.  
- Case-insensitive uniqueness checks.  
- UTC timestamps in ISO format.

## Business Rules
- Max 5 active rentals per user.  
- Cannot delete rented books or users with active rentals.  
- Always at least one admin.  
- Reserved username: `admin`.  

## Access Control
- **USER**: browse/search, rent/return, view/edit profile.  
- **ADMIN**: all user functions + manage books/users, import/export.

## Technical Requirements
- **Spring Boot**, Repository pattern, JSON persistence, constructor injection.  
- Components: `@SpringBootApplication`, `@RestController`, `@Service`, `@Repository`.  
- **Testing**: JUnit 5, Mockito.  
- **Docs**: Swagger UI (`/swagger-ui.html`).

## API Endpoints (Highlights)
- `POST /auth/register` → register  
- `POST /auth/login` → login  
- `GET /books` → search/list  
- `POST /books/{id}/rent` → rent  
- `POST /books/{id}/return` → return  
- `GET /admin/export` / `POST /admin/import`【97†source】.

---
✅ **Success Criteria**: All auth flows, CRUD for books/users, import/export, test coverage, clean architecture.
