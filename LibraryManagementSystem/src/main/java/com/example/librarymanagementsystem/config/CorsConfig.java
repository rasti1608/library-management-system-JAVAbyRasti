package com.example.librarymanagementsystem.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS configuration for Library Management System
 *
 * - Allows ONLY explicit origins (no wildcard) because we use cookies (credentials=true).
 * - Works for both:
 *      1) Production frontend (Netlify)
 *      2) Local development (localhost / common dev ports)
 * - Spring Security 6.x friendly: we expose a CorsFilter bean; no deprecated http.cors().
 *
 * How it flows:
 *   Browser (Netlify or localhost) --> OPTIONS/actual request
 *   Spring CorsFilter reads this config and sets the right CORS headers
 *   Browser accepts cookie (JSESSIONID) because origin matches AND SameSite=None; Secure
 */
@Configuration
public class CorsConfig {

    // 1) Put your exact Netlify origin here (NO trailing slash)
    private static final String NETLIFY = "https://melodious-mousse-c2a470.netlify.app";

    // 2) Local dev origins we allow while you’re building & testing
    //    (Keep if you want to test locally; remove them if you want a locked-down prod)
    private static final List<String> DEV_ORIGINS = List.of(
            "http://localhost",
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1",
            "http://127.0.0.1:5500"
    );

    /**
     * Defines the CORS policy:
     *  - exact origins (Netlify + localhost dev)
     *  - methods/headers allowed
     *  - credentials enabled for cookie-based sessions
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // Allowed origins: add Netlify + dev origins (no "*", because credentials=true)
        cfg.setAllowedOrigins(merge(NETLIFY, DEV_ORIGINS));

        // Typical REST verbs
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow any header from the browser (e.g., Content-Type, Authorization)
        cfg.setAllowedHeaders(List.of("*"));

        // Must be true for browser to send/receive cookies across origins
        cfg.setAllowCredentials(true);

        // Preflight cache duration (seconds)
        cfg.setMaxAge(3600L);

        // Apply this config to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Registers the actual CORS filter that Spring Security will honor.
     * (No FilterRegistrationBean wrapper; type is exactly CorsFilter.)
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    // ---- helpers -------------------------------------------------------------

    private static List<String> merge(String primary, List<String> extra) {
        // Combine primary origin + dev list into one immutable list
        return List.of(primary, extra.get(0),
                extra.size() > 1 ? extra.get(1) : primary,
                extra.size() > 2 ? extra.get(2) : primary,
                extra.size() > 3 ? extra.get(3) : primary,
                extra.size() > 4 ? extra.get(4) : primary);
    }
}
