package com.skillbook.platform.controller;

import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling user registration functionality.
 * Provides endpoints for creating new user accounts in the system.
 *
 * @author mariya-koles
 * @version 1.0
 * @since 2025-03
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Processes new user registration requests.
     * Validates user input, checks for existing usernames, and securely stores user data.
     *
     * @param user the user registration data transfer object
     * @return ResponseEntity with registration status message
     * @HTTP 200 OK if registration is successful
     * @HTTP 400 Bad Request if username is empty or already exists
     * @throws jakarta.validation.ConstraintViolationException if validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<?> processRegistration(@Valid @RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
