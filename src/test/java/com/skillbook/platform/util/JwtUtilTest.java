package com.skillbook.platform.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Set the JWT secret using reflection for testing - needs to be at least 512 bits for HS512
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "mySecretKeyForTestingPurposesItShouldBeLongEnoughForHS512AlgorithmAndMustBeAtLeast512Bits");
        jwtUtil.init();
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT tokens have 3 parts separated by dots
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void testExtractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        String extractedUsername = jwtUtil.extractUsername(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        boolean isValid = jwtUtil.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtUtil.validateToken(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken() {
        boolean isValid = jwtUtil.validateToken(null);
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken() {
        boolean isValid = jwtUtil.validateToken("");
        
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_MalformedToken() {
        String malformedToken = "this.is.not.a.valid.jwt.token";
        
        boolean isValid = jwtUtil.validateToken(malformedToken);
        
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void testGenerateTokenWithDifferentUsernames() {
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");
        
        // Tokens should be different for different usernames
        assertNotEquals(token1, token2);
        
        // But should extract correct usernames
        assertEquals("user1", jwtUtil.extractUsername(token1));
        assertEquals("user2", jwtUtil.extractUsername(token2));
    }

    @Test
    void testTokenValidationAfterGeneration() {
        String[] usernames = {"alice", "bob", "charlie", "admin", "instructor"};
        
        for (String username : usernames) {
            String token = jwtUtil.generateToken(username);
            
            // Token should be valid
            assertTrue(jwtUtil.validateToken(token));
            
            // Should extract correct username
            assertEquals(username, jwtUtil.extractUsername(token));
        }
    }

    @Test
    void testTokenStructure() {
        String token = jwtUtil.generateToken("testuser");
        String[] parts = token.split("\\.");
        
        // JWT should have exactly 3 parts: header.payload.signature
        assertEquals(3, parts.length);
        
        // Each part should not be empty
        for (String part : parts) {
            assertFalse(part.isEmpty());
        }
    }

    @Test
    void testMultipleTokenGeneration() throws InterruptedException {
        String username = "sameuser";
        String token1 = jwtUtil.generateToken(username);
        
        // Add a longer delay to ensure different timestamps
        Thread.sleep(1100); // More than 1 second to ensure different iat (issued at) timestamp
        
        String token2 = jwtUtil.generateToken(username);
        
        // Tokens should be different due to different timestamps
        assertNotEquals(token1, token2);
        
        // But both should be valid and extract the same username
        assertTrue(jwtUtil.validateToken(token1));
        assertTrue(jwtUtil.validateToken(token2));
        assertEquals(username, jwtUtil.extractUsername(token1));
        assertEquals(username, jwtUtil.extractUsername(token2));
    }

    @Test
    void testJwtUtilInit() {
        // Create a new instance to test init method
        JwtUtil newJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(newJwtUtil, "jwtSecret", "anotherSecretKeyForTestingPurposesItShouldBeLongEnoughForHS512AlgorithmAndMustBeAtLeast512BitsLong");
        
        // Should not throw any exception
        assertDoesNotThrow(() -> newJwtUtil.init());
        
        // Should be able to generate tokens after init
        String token = newJwtUtil.generateToken("testuser");
        assertNotNull(token);
        assertTrue(newJwtUtil.validateToken(token));
    }
} 