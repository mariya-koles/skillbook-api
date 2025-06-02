package com.skillbook.platform.controller;



import com.skillbook.platform.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authenticationManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        if (request.username == null || request.password == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username and password are required");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.username,
                    request.password
                )
            );
            
            if (auth.isAuthenticated()) {
                final String jwt = jwtUtil.generateToken(request.username);
                return ResponseEntity.ok(new AuthResponse(jwt));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    static class AuthRequest {
        public String username;
        public String password;
    }

    static class AuthResponse {
        public String token;
        public AuthResponse(String token) { this.token = token; }
    }
}