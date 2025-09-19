package com.example.librarymanagementsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * HealthController
 *
 * Very simple controller that exposes a "/health" endpoint.
 * Purpose:
 * - Quick way to check if the application is alive and responding.
 * - Often used by monitoring tools or just for manual "is it up?" checks.
 */
@RestController // Marks this class as a REST controller (returns JSON instead of HTML)
public class HealthController {

    /**
     * GET /health
     *
     * Returns a small JSON object with:
     * - status: always "UP" if this code is running
     * - timestamp: current server time
     */
    @GetMapping("/health") // Maps GET requests for /health to this method
    public Map<String, Object> health() {
        // Return a simple JSON map (Spring automatically converts Map -> JSON)
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
