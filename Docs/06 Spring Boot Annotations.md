# 📚 Library Management System – Spring Boot Annotations Reference Guide

This guide provides a **comprehensive overview** of all Spring Boot and testing annotations used in the Library Management System project.  
It explains their purpose, shows where they are applied in the codebase, and gives examples.

---

## 🚀 Core Spring Framework Annotations

### `@SpringBootApplication`
✨ Combines three key annotations to bootstrap the application:
- `@Configuration` 🛠 – Marks the class as a configuration class  
- `@EnableAutoConfiguration` ⚡ – Enables Spring Boot auto-configuration  
- `@ComponentScan` 🔍 – Scans the package and subpackages for Spring components  

📌 **Used in:** `LibraryManagementSystemApplication.java`  

```java
@SpringBootApplication
public class LibraryManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }
}
```

---

## 🧩 Dependency Injection Annotations

### `@Component`
🏷 Generic stereotype annotation for any Spring-managed component.  
✅ Spring creates a singleton bean in the context.

📌 **Used in:** `AuthHelper`, `CacheHelper`, `ValidationHelper`  

```java
@Component
public class AuthHelper {
    // Spring will create and manage this instance
}
```

### `@Service`
⚙️ Specialized `@Component` for service-layer business logic.  
📌 **Used in:** `AuthService`, `BookService`, `UserService`, `RentalService`  

```java
@Service
public class AuthService {
    // Business logic for user authentication
}
```

### `@Repository`
💾 Specialized `@Component` for the data access layer.  
🔄 Enables exception translation.  
📌 **Used in:** `JsonUserRepository`, `JsonBookRepository`, `JsonRentalRepository`  

```java
@Repository
public class JsonUserRepository implements UserRepository {
    // Data access logic for users
}
```

### `@Configuration` + `@Bean`
⚙️ Used for custom configuration and bean definitions.  
📌 **Used in:** `SecurityConfig.java`  

```java
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
```

---

## 🌐 Web Layer Annotations

### `@RestController`
🌍 Combines `@Controller` + `@ResponseBody`.  
📌 **Used in:** `AuthController`, `BookController`, `UserController`, `AdminController`, `HealthController`  

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    // REST API endpoints for authentication
}
```

### `@RequestMapping`
🛣 Maps HTTP requests to controllers or handler methods.  

### `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
➡️ Shortcuts for specific HTTP methods.  

Example:
```java
@GetMapping("/me")
public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
    // Handle GET /users/me
}
```

### `@RequestBody`
📦 Maps request body JSON → Java object.  

### `@PathVariable`
🗝 Extracts values from URL path.  

### `@RequestParam`
🔎 Maps query parameters → method parameters.  

---

## 🧪 Testing Annotations

### `@ExtendWith(MockitoExtension.class)`
🤝 Integrates Mockito with JUnit 5.  

### `@Mock`  
🎭 Creates mock objects for dependencies.  

### `@InjectMocks`  
📦 Injects mocks into the tested class.  

### `@BeforeEach`  
⏱ Runs before every test.  

### `@Test`  
🧪 Marks a test method.  

### `@TempDir`  
📂 Creates temporary test directory (auto-cleanup).  

---

## 📖 API Documentation Annotations (Swagger/OpenAPI)

### `@Tag`
🏷 Groups related endpoints in Swagger UI.  

### `@Operation`
📝 Documents endpoint summary & description.  

### `@ApiResponses` + `@ApiResponse`
📡 Lists possible response codes.  

### `@Parameter`
🖊 Documents method parameters in API docs.  

---

## ✅ Summary

This project demonstrates key Spring Boot concepts through **annotations**:  

- 🧩 **Dependency Injection** → `@Component`, `@Service`, `@Repository`  
- 🌐 **Web Layer** → `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`  
- ⚙️ **Configuration** → `@Configuration`, `@Bean`  
- 🧪 **Testing** → `@Mock`, `@InjectMocks`, `@Test`  
- 📖 **Documentation** → `@Tag`, `@Operation`, `@ApiResponse`  

💡 Using annotations makes the application **cleaner, maintainable, and less boilerplate-heavy**.

---

🚀 Happy Coding!  
