# 📚 Library Management System – Business Requirements

## Project Overview
A **Spring Boot web application** for managing library operations including book catalog management, user authentication, and rental tracking.  
The system demonstrates **dependency injection patterns** and **role-based access control**【96†source】.

## Library Model
- Specialized collections (rare books, reference materials, special items).  
- **Single-copy-per-title** policy for simple availability tracking.  
- No inventory complexity, focus on Spring concepts.

## Technical Stack
- **Framework**: Spring Boot  
- **Data Storage**: JSON files (no database)  
- **Authentication**: Custom login system with role-based access  
- **Testing**: JUnit 5 + Mockito  
- **Architecture**: Repository pattern + dependency injection

## User Management
- **User Registration**: username, password, email → role = USER by default.  
- **Authentication**: username/password, session-based.  
- **Bootstrap Admin**: admin/admin123, protected, cannot be deleted/demoted.

## Role-Based Access Control
- **USER**: Browse/search books, rent/return, view/edit own profile.  
- **ADMIN**: All user capabilities + manage books/users, import/export JSON.

## Data Storage Structure
- `users.json` → user accounts  
- `books.json` → book catalog  
- `rentals.json` → rental history

## Book Management
- **Status**: AVAILABLE or RENTED  
- **Single Copy**: one title/author per copy  
- Rental system: rent → mark RENTED, return → mark AVAILABLE

## Import/Export
- Admin-only, export catalog to JSON, import with duplicate checks.  

## Architecture
- Repository interfaces, service layer with DI, controllers, utilities.  
- REST endpoints with role-based access.

## Development Phases
1. Core structure (entities, repositories, services)  
2. Authentication (register/login)  
3. Book management (CRUD, rental)  
4. Admin features (users, import/export)  
5. Testing & documentation

---
✅ **Success Criteria**: Functional authentication, clean architecture, import/export capability, test coverage【96†source】.
