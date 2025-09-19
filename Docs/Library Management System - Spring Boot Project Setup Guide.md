# Library Management System — Phase 1.1: Initialize & Run (Step‑by‑Step)

This doc shows **exactly** how to create and run the LMS Spring Boot skeleton on a fresh machine. It’s written for beginners—follow it line‑by‑line and you’ll be up in minutes.

---

## ✅ Prerequisites
- **IntelliJ IDEA** (Ultimate or Community)  
- **JDK 21** installed and selected in IntelliJ

> You do **not** need Maven installed globally. The project uses the **Maven Wrapper** (`mvnw`), which ships with the repo.

---

## 1) Create the Spring Boot project (IntelliJ wizard)

**New Project → Spring Boot**

- **Type:** `Maven`  
- **Group:** `com.example`  
- **Artifact:** `library-management-system`  
- **Package name:** `com.example.librarymanagementsystem`  
- **JDK:** `21`  
- **Spring Boot:** `3.5.5`  
- **Packaging:** `Jar`

**Dependencies to check now:**
- **Web →** `Spring Web`
- **Security →** `Spring Security`
- **I/O →** `Validation`
- **Developer Tools →** `Spring Boot DevTools` *(optional, handy in dev)*

Click **Create**.

> 💡 Avoid nested Git: don’t check *Create Git repository* in the wizard if your parent folder is already a repo.

---

## 2) Add Swagger/OpenAPI (for live API docs)

The wizard doesn’t include Swagger. Add this to `pom.xml` inside `<dependencies>`:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.6.0</version>
</dependency>
```

After the first run, you’ll open **`/swagger-ui.html`** to click‑test endpoints.

---

## 3) Create the base classes (with teaching comments)

> All files live under: `src/main/java/com/example/librarymanagementsystem`

### `Application.java` — the entrypoint
```java
package com.example.librarymanagementsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APPLICATION ENTRYPOINT (Spring Boot)
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * Component scanning starts at this package and includes subpackages.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

  public static void main(String[] args) {
    // Boot creates the application context, performs auto‑config, and then calls run(...)
    SpringApplication.run(Application.class, args);
  }

  @Override public void run(String... args) {
    System.out.println("LMS up — skeleton running. Swagger at /swagger-ui.html");
  }
}
```

### `config/SecurityConfig.java` — minimal security for dev
```java
package com.example.librarymanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SECURITY CONFIGURATION (dev‑friendly)
 * - Permits Swagger and /health without login
 * - Keeps simple login for now (HTTP Basic + default form)
 * - BCrypt for password hashing (will be used when we add users)
 */
@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain api(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable()) // simple JSON API during early dev
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/auth/**", "/health").permitAll()
        .requestMatchers("/admin/**", "/users/**").hasRole("ADMIN")
        .requestMatchers("/books/**", "/users/me/**").hasAnyRole("USER","ADMIN")
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults())
      .formLogin(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
}
```

### `dto/PageResponse.java` — pagination envelope
```java
package com.example.librarymanagementsystem.dto;

import java.util.List;

/** Pagination wrapper returned by list endpoints. */
public class PageResponse<T> {
  public final List<T> content;
  public final int page, size, totalPages;
  public final long total;
  public final boolean hasNext, hasPrev;

  public PageResponse(List<T> content, int page, int size, long total) {
    this.content = content;
    this.page = page; this.size = size; this.total = total;
    int safe = Math.max(1, size);
    this.totalPages = (int)Math.ceil(total / (double)safe);
    this.hasPrev = page > 0;
    this.hasNext = page < Math.max(0, totalPages - 1);
  }
}
```

### *(Optional)* `web/HealthController.java` — quick smoke check
```java
package com.example.librarymanagementsystem.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Simple endpoint to prove the app is running. */
@RestController
public class HealthController {
  @GetMapping("/health") public String health() { return "OK"; }
}
```

---

## 4) Configure the server port (avoid 8080 conflicts)

Create the real config file under **resources**:

`src/main/resources/application.properties`
```properties
# Run this app on 8085 (change if needed)
server.port=8085
```

> If you accidentally created a file named `properties` under `src/main/java`, delete it. The correct location is **src/main/resources/application.properties**.

---

## 5) Run the app

### A) From IntelliJ
- Open `Application.java` → **Run ‘Application.main()’**
- ✅ Expected log bits:
  - `Tomcat started on port 8085`
  - `LMS up — skeleton running. Swagger at /swagger-ui.html`

### B) From terminal (project root)
Using the Maven **wrapper** that’s already in the project:

```bash
# Windows PowerShell
.\mvnw -q -DskipTests package
java -jar (Get-ChildItem target\*.jar).FullName
```

```bash
# macOS/Linux
./mvnw -q -DskipTests package
java -jar target/*.jar
```

---

## 6) Verify

- Open **http://localhost:8085/health** → `OK`
- Open **http://localhost:8085/swagger-ui.html** → Swagger UI loads  
  (If it 403/404s, re-check the Swagger dependency and SecurityConfig permit list.)

---

## 7) Expected project tree (after Step 1.1)

```
library-management-system/
└─ src/
   └─ main/
      ├─ java/com/example/librarymanagementsystem/
      │  ├─ Application.java
      │  ├─ config/SecurityConfig.java
      │  ├─ dto/PageResponse.java
      │  └─ web/HealthController.java   (optional)
      └─ resources/
         └─ application.properties
pom.xml
mvnw / mvnw.cmd
```

---

## Troubleshooting (fast answers)

- **Port 8080 in use:** set `server.port=8085` in `application.properties`.  
- **Two main classes / duplicate `@SpringBootApplication`:** keep **one** (prefer `Application`), delete the other, then run again.  
- **Swagger doesn’t load:** add the springdoc dependency; permit `/swagger-ui/**` and `/v3/api-docs/**` in SecurityConfig.  
- **Created wrong “properties” file under `java`:** delete it; create `src/main/resources/application.properties`.  
- **Run config still points to old class:** right‑click `Application.java` → **Run**, then delete the old run configuration.

---

## Next phases

- **Phase 1.2:** create base packages and empty models (`Book`, `User`, `Rental`).  
- **Phase 2:** JSON repositories (atomic writes) + services.  
- **Phase 3:** Controllers + Swagger annotations + pagination.  
- **Phase 4:** Auth/register/login + role rules.  
- **Phase 5:** Admin import/export (append + duplicate skip) and tests.

You’re done with **Phase 1.1** ✅ — project initialized, runs on 8085, and Swagger/health are reachable.
