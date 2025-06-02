package com.skillbook.platform.controller;


import com.skillbook.platform.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Auth controller.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    /**
     * Instantiates a new Auth controller.
     *
     * @param authManager the auth manager
     * @param jwtUtil     the jwt util
     */
    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authenticationManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login response entity.
     *
     * @param request the request
     * @return the response entity
     */
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

    /**
     * The type Auth request.
     */
    static class AuthRequest {
        /**
         * The Username.
         */
        public String username;
        /**
         * The Password.
         */
        public String password;
    }

    /**
     * The type Auth response.
     */
    static class AuthResponse {
        /**
         * The Token.
         */
        public String token;

        /**
         * Instantiates a new Auth response.
         *
         * @param token the token
         */
        public AuthResponse(String token) {
            this.token = token;
        }
    }
}