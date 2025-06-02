package com.skillbook.platform.model;

import com.skillbook.platform.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LEARNER)
                .enrolledCourses(new HashSet<>())
                .build();

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.LEARNER, user.getRole());
        assertNotNull(user.getEnrolledCourses());
    }

    @Test
    void testUserNoArgsConstructor() {
        User user = new User();
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getRole());
        assertNotNull(user.getEnrolledCourses());
    }

    @Test
    void testUserAllArgsConstructor() {
        Set<Course> courses = new HashSet<>();
        byte[] photo = "photo".getBytes();
        
        User user = new User(1L, "testuser", "test@example.com", "password123", 
                            "John", "Doe", Role.LEARNER, photo, courses);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.LEARNER, user.getRole());
        assertArrayEquals(photo, user.getProfilePhoto());
        assertEquals(courses, user.getEnrolledCourses());
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        Set<Course> courses = new HashSet<>();
        byte[] photo = "photo".getBytes();

        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.LEARNER);
        user.setProfilePhoto(photo);
        user.setEnrolledCourses(courses);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(Role.LEARNER, user.getRole());
        assertArrayEquals(photo, user.getProfilePhoto());
        assertEquals(courses, user.getEnrolledCourses());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder().id(1L).username("test").build();
        User user2 = User.builder().id(1L).username("different").build();
        User user3 = User.builder().id(2L).username("test").build();
        User user4 = User.builder().id(1L).username("test").build();

        // Same ID should be equal
        assertEquals(user1, user2);
        assertEquals(user1, user4);
        
        // Different ID should not be equal
        assertNotEquals(user1, user3);
        
        // Null check
        assertNotEquals(user1, null);
        
        // Same object should be equal
        assertEquals(user1, user1);
        
        // Hash code consistency
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testValidation_ValidUser() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankUsername() {
        User user = User.builder()
                .username("")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_NullUsername() {
        User user = User.builder()
                .username(null)
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_InvalidEmail() {
        User user = User.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_BlankEmail() {
        User user = User.builder()
                .username("testuser")
                .email("")
                .password("password123")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size()); // Only @NotBlank triggers for empty string
        assertTrue(violations.iterator().next().getMessage().contains("required"));
    }

    @Test
    void testValidation_BlankPassword() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Password is required", violations.iterator().next().getMessage());
    }

    @Test
    void testToString() {
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LEARNER)
                .build();

        String toString = user.toString();
        
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
        assertTrue(toString.contains("LEARNER"));
        // profilePhoto and enrolledCourses should be excluded from toString
        assertFalse(toString.contains("profilePhoto"));
        assertFalse(toString.contains("enrolledCourses"));
    }

    @Test
    void testCourseEnrollment() {
        User user = new User();
        Course course1 = Course.builder().id(1L).title("Java 101").build();
        Course course2 = Course.builder().id(2L).title("Spring Boot").build();

        user.getEnrolledCourses().add(course1);
        user.getEnrolledCourses().add(course2);

        assertEquals(2, user.getEnrolledCourses().size());
        assertTrue(user.getEnrolledCourses().contains(course1));
        assertTrue(user.getEnrolledCourses().contains(course2));
    }
} 