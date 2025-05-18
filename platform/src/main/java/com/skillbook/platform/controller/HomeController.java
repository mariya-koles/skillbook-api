package com.skillbook.platform.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

/**
 * Root controller for the Skillbook API.
 * Provides basic health check and API status information.
 *
 * @author mariya-koles
 * @version 1.0
 * @since 2025-03
 */
@RestController
public class HomeController {

    /**
     * Health check endpoint that confirms the API is running.
     * This endpoint is publicly accessible and requires no authentication.
     *
     * @return ResponseEntity containing a status message
     * @HTTP 200 OK if the API is running successfully
     */
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Skillbook API is running");
    }
}
