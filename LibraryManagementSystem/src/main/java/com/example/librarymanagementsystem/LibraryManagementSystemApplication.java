package com.example.librarymanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MAIN APPLICATION ENTRYPOINT
 *
 * - This is the canonical Spring Boot "main class".
 * - It is the ONLY class annotated with @SpringBootApplication.
 * - Responsibilities:
 *   1. Bootstraps the Spring context
 *   2. Enables auto-configuration
 *   3. Triggers component scanning for the package com.example.librarymanagementsystem and all subpackages
 *
 * Why keep this separate and minimal?
 * - Convention: tools and frameworks know this is the app’s entrypoint
 * - Clarity: keeps the "bootstrapping" concern apart from business code
 * - Extensible: if needed, we can add global @Bean definitions here later
 */
@SpringBootApplication
public class LibraryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }

}

