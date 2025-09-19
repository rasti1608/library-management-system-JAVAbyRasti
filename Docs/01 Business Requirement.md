# 📚 Library Management System – Business Requirements Document

---

## 🔎 Project Overview
A **Spring Boot web application** for managing library operations including:  
- 📖 Book catalog management  
- 👤 User authentication  
- 📊 Rental tracking  

This system demonstrates **enterprise-level dependency injection patterns** and **role-based access control**.

---

## 🏛️ Library Model
- Designed for specialized collections (rare books, references, special items).  
- **Single-copy-per-title** policy → simple availability tracking.  
- No inventory complexity → focus remains on Spring Boot concepts.

---

## ⚙️ Technical Stack
- **Framework**: Spring Boot  
- **Data Storage**: JSON files *(no database)*  
- **Authentication**: Custom login system with role-based access  
- **Testing**: JUnit 5 + Mockito  
- **Architecture**: Repository pattern with dependency injection  

---

## 👥 User Management

### 👤 User Registration
- ✅ New users can only register with **USER** role  
- 📧 Required fields: *username, password, email*  
- 🔒 No role selection during registration (security best practice)  

### 🔑 User Authentication
- Standard **username + password** login  
- Session-based authentication  
- Role-based access applied after login  

### 🛡️ Bootstrap Admin Account
- Default Admin: `username = admin`, `password = admin123`  
- Marked as **protected** in `users.json`  
- Cannot be deleted or demoted  
- Ensures system always has at least one admin  

---

## 🔐 Role-Based Access Control

### USER Capabilities
- 🔎 Browse & search books (by title or author)  
- 📖 Rent available books *(single copy per book)*  
- 📂 View list of currently rented books  
- 🔄 Return rented books  
- ✏️ Edit own account info *(username, email, password)*  
- 👀 View own account profile  

### ADMIN Capabilities
Includes all USER capabilities **plus**:  
- ➕ Add new books to catalog  
- ✏️ Edit book information  
- ❌ Delete books from catalog  
- 📥 Import additional books *(append-only, skip duplicates)*  
- 📤 Export catalog to JSON file  
- 👥 Manage all users (view, edit, promote/demote, delete — except protected accounts)  

---

## 🗂️ Data Storage Structure
Three JSON files handle persistence:  

1. **users.json** → user accounts  
2. **books.json** → catalog of books  
3. **rentals.json** → rental history  

---

## 📖 Book Management

- **Statuses**: `AVAILABLE` or `RENTED`  
- **Single-copy policy**: one unique title/author combination  
- **Rental flow**:  
  - User rents book → status = `RENTED`  
  - User returns book → status = `AVAILABLE`  

---

## 📤 Import & 📥 Export

- **Export (Admin)**: full catalog → JSON (timestamped, e.g. `library_export_2025-09-16.json`)  
- **Import (Admin)**: append-only JSON upload  
  - Skips duplicates (same *title + author*)  
  - Provides summary: *books added vs skipped*  

---

## 🏗️ Technical Architecture

- **Repository Interfaces** → `UserRepository`, `BookRepository`, `RentalRepository`  
- **Service Layer** → `BookService`, `UserService`, `RentalService`  
- **Spring Components**:  
  - `@SpringBootApplication` → main app  
  - `@RestController` → APIs  
  - `@Service` → business logic  
  - `@Repository` → persistence  

---

## 📅 Development Phases

1. **Core Structure** – Entities, repositories, services  
2. **Authentication** – Register/login with sessions  
3. **Book Management** – CRUD, rental/return  
4. **Admin Features** – Manage users, import/export  
5. **Testing & Docs** – Unit tests, integration tests, API docs  

---

## ✅ Success Criteria
- Functional **authentication & role-based control**  
- Clean, maintainable **architecture**  
- Working **import/export** capability  
- Comprehensive **test coverage**  
- Professional documentation & presentation  

---
