# 🚀 Real Demo -- Library Management System

## 🌐 Live Demo

👉 **[Open the Demo
Here](https://melodious-mousse-c2a470.netlify.app/index.html)**

This live deployment demonstrates how the **frontend pages** (login,
register, books, admin) are fully integrated with the **backend APIs**.

Every page includes **educational tooltips** that reveal:
- Client-side validation rules
- JSON payloads sent to the backend
- Expected responses and error codes
- How authentication and roles are enforced

This way the demo works as both a **functional system** and a **learning
tool**.

------------------------------------------------------------------------

## 🧑‍💻 What This Demo Shows

-   🔑 User registration and login with session management
-   📚 Book catalog browsing, searching, renting, and returning
-   👤 User profile management
-   👨‍💻 Admin tools: manage books, users, import/export catalog
-   📡 Real API calls visible through browser DevTools + explained via
    tooltips

------------------------------------------------------------------------

## 📡 Available APIs

### Authentication

-   **POST /auth/register** → Register new account (USER role)
-   **POST /auth/login** → Authenticate user, create session
-   **POST /auth/logout** → Invalidate current session

### Users

-   **GET /users/me** → Get my profile
-   **PUT /users/me** → Update my profile
-   **GET /users/me/rentals** → Get my rental history
-   **GET /users** → List all users (Admin only)
-   **PUT /users/{id}** → Update any user (Admin only)
-   **DELETE /users/{id}** → Delete user (Admin only)
-   **POST /users/{id}/promote** → Promote user to admin (Admin only)
-   **POST /users/{id}/demote** → Demote admin to user (Admin only)

### Books

-   **GET /books** → Search/list books (title/author filters +
    pagination)
-   **POST /books** → Add new book (Admin only)
-   **POST /books/{id}/rent** → Rent a book
-   **POST /books/{id}/return** → Return a rented book
-   **GET /books/my-rentals** → Get my active rentals

### Admin

-   **PUT /admin/books/{id}** → Update book info
-   **DELETE /admin/books/{id}** → Delete book (cannot delete rented)
-   **POST /admin/books** → Add book
-   **POST /admin/import** → Import catalog from JSON file
-   **GET /admin/export** → Export full catalog as JSON

### Health

-   **GET /health** → Service status

------------------------------------------------------------------------

## 🎓 Why This Matters

Most demos only show that "it runs."
This one goes further --- it **teaches how APIs really work**:
- Frontend → API → Backend → Persistence → Response.
- Tooltips turn every form field into a **mini-lesson** about
validation, payloads, and errors.
- Anyone can open the demo link and see both the **user experience** and
the **developer's perspective**.
