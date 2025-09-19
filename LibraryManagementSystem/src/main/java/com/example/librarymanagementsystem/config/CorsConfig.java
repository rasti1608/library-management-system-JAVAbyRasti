package com.example.librarymanagementsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for Library Management System
 *
 * Enables Cross-Origin Resource Sharing (CORS) to allow frontend applications
 * hosted on different domains to access our REST API endpoints.
 *
 * This configuration supports both local development (localhost:3000) and
 * production deployment (Netlify hosted frontend).
 *
 * @author Rastislav Toscak
 * @version 1.0
 * @since 2025-01-22
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mapping for all API endpoints.
     *
     * Allows requests from:
     * - Local development server (http://localhost)
     * - Netlify production deployment (https://melodious-mousse-c2a470.netlify.app)
     *
     * Supports all standard HTTP methods and enables credential sharing
     * for session-based authentication.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Apply to all endpoints
                .allowedOriginPatterns("*")                                    // Allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")     // Standard HTTP methods
                .allowCredentials(true)                                        // Enable session cookies
                .allowedHeaders("*")                                           // Accept all headers
                .maxAge(3600);                                                 // Cache preflight for 1 hour
    }
}