# 📚 Library Management System – Final Project
[![Swagger UI](https://img.shields.io/badge/API-Docs-green)](https://library-management-system-rasti-production.up.railway.app/swagger-ui/index.html)

---

## What the System Does
- 👤 **User Accounts**
  - Anyone can register as a **User**
  - Default **Admin** is always present (can’t be deleted or demoted)
  - Users log in with username & password  
  - Roles decide what you can do (User vs Admin)

- 📖 **Books**
  - Each title exists as **one unique copy**  
  - Status is always clear: **AVAILABLE** or **RENTED**  
  - Users can browse, search, rent, and return  
  - Admins can add, edit, or delete books from the catalog  

- 🔑 **Roles**
  - **User** → search, rent, return, edit own profile  
  - **Admin** → everything Users can do + manage books, manage users, import/export data  

- 📂 **Data Storage**
  - Everything is stored in **JSON files** (`users.json`, `books.json`, `rentals.json`)  
  - Simple, transparent, and easy to inspect  

- 📤 **Import / Export**
  - Admins can export the entire catalog into a JSON file  
  - Admins can import more books (duplicates skipped)  

---

## Technical Stack
- **Framework**: Spring Boot  
- **Storage**: JSON files (no database)  
- **Authentication**: Custom login with roles  
- **Architecture**: Repository + Service + Controller layers  
- **Testing**: JUnit 5 & Mockito  

---

## Deployment Notes

**For deployment on Railway (or similar cloud hosting platforms):**
- Use the `working_Final_v.2.0-sept20` branch
- This branch has JSON data files properly configured for cloud environments where file system access is restricted

**For local development:**
- The `main` branch works perfectly fine
- JSON files can be read/written from any location in your local environment

**Why the difference?**
Cloud platforms like Railway restrict file system write access outside of specific directories. The `working_Final_v.2.0-sept20` branch has the JSON files positioned correctly for these restrictions, while the main branch is optimized for local development flexibility.

---

## Why This Project?
This system models a **specialized library** where each book is unique.  
It keeps the focus on **clear business rules** and **clean Spring concepts** instead of inventory complexity.  

The project proves:  
- You can build a professional-grade application in a short timeframe.  
- Java & Spring skills are applied in a real-world scenario.  
- Everything is backed by unit tests, documentation, and proper structure.  

---

## Roadmap
1. **Core Structure** – Entities, repositories, services  
2. **Authentication** – Registration, login, role checks  
3. **Book Management** – CRUD, search, rent/return  
4. **Admin Tools** – Manage users, import/export books  
5. **Testing & Docs** – Unit tests, integration tests, user guide  

---

## Status
🚀 Work in progress — but the foundation is here.  
This isn’t just an idea. It’s being built, line by line, commit by commit.  

Stay tuned.  
"# java-training-final-project-library-management-system" 
