package com.skillbook.platform.model;

import com.skillbook.platform.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    void testCourseBuilder() {
        User instructor = User.builder()
                .id(1L)
                .username("instructor")
                .role(Role.INSTRUCTOR)
                .build();

        LocalDateTime startTime = LocalDateTime.now();
        Set<User> enrolledUsers = new HashSet<>();

        Course course = Course.builder()
                .id(1L)
                .title("Java Fundamentals")
                .description("Learn Java basics")
                .longDescription("A comprehensive course on Java fundamentals")
                .category("Programming")
                .instructor(instructor)
                .startTime(startTime)
                .durationMinutes(120)
                .enrolledUsers(enrolledUsers)
                .build();

        assertEquals(1L, course.getId());
        assertEquals("Java Fundamentals", course.getTitle());
        assertEquals("Learn Java basics", course.getDescription());
        assertEquals("A comprehensive course on Java fundamentals", course.getLongDescription());
        assertEquals("Programming", course.getCategory());
        assertEquals(instructor, course.getInstructor());
        assertEquals(startTime, course.getStartTime());
        assertEquals(120, course.getDurationMinutes());
        assertEquals(enrolledUsers, course.getEnrolledUsers());
    }

    @Test
    void testCourseNoArgsConstructor() {
        Course course = new Course();
        
        assertNull(course.getId());
        assertNull(course.getTitle());
        assertNull(course.getDescription());
        assertNull(course.getLongDescription());
        assertNull(course.getCategory());
        assertNull(course.getInstructor());
        assertNull(course.getStartTime());
        assertEquals(0, course.getDurationMinutes());
        assertNotNull(course.getEnrolledUsers());
    }

    @Test
    void testCourseAllArgsConstructor() {
        User instructor = User.builder().id(1L).username("instructor").build();
        LocalDateTime startTime = LocalDateTime.now();
        Set<User> enrolledUsers = new HashSet<>();

        Course course = new Course(1L, "Java Course", "Description", "Long Description", 
                                  "Programming", instructor, startTime, 120, enrolledUsers);

        assertEquals(1L, course.getId());
        assertEquals("Java Course", course.getTitle());
        assertEquals("Description", course.getDescription());
        assertEquals("Long Description", course.getLongDescription());
        assertEquals("Programming", course.getCategory());
        assertEquals(instructor, course.getInstructor());
        assertEquals(startTime, course.getStartTime());
        assertEquals(120, course.getDurationMinutes());
        assertEquals(enrolledUsers, course.getEnrolledUsers());
    }

    @Test
    void testGettersAndSetters() {
        Course course = new Course();
        User instructor = User.builder().id(1L).username("instructor").build();
        LocalDateTime startTime = LocalDateTime.now();
        Set<User> enrolledUsers = new HashSet<>();

        course.setId(1L);
        course.setTitle("Spring Boot Course");
        course.setDescription("Learn Spring Boot");
        course.setLongDescription("A detailed course on Spring Boot framework");
        course.setCategory("Web Development");
        course.setInstructor(instructor);
        course.setStartTime(startTime);
        course.setDurationMinutes(180);
        course.setEnrolledUsers(enrolledUsers);

        assertEquals(1L, course.getId());
        assertEquals("Spring Boot Course", course.getTitle());
        assertEquals("Learn Spring Boot", course.getDescription());
        assertEquals("A detailed course on Spring Boot framework", course.getLongDescription());
        assertEquals("Web Development", course.getCategory());
        assertEquals(instructor, course.getInstructor());
        assertEquals(startTime, course.getStartTime());
        assertEquals(180, course.getDurationMinutes());
        assertEquals(enrolledUsers, course.getEnrolledUsers());
    }

    @Test
    void testEqualsAndHashCode() {
        Course course1 = Course.builder().id(1L).title("Java Course").build();
        Course course2 = Course.builder().id(1L).title("Different Title").build();
        Course course3 = Course.builder().id(2L).title("Java Course").build();
        Course course4 = Course.builder().id(1L).title("Java Course").build();

        // Same ID should be equal
        assertEquals(course1, course2);
        assertEquals(course1, course4);
        
        // Different ID should not be equal
        assertNotEquals(course1, course3);
        
        // Null check
        assertNotEquals(course1, null);
        
        // Same object should be equal
        assertEquals(course1, course1);
        
        // Hash code consistency
        assertEquals(course1.hashCode(), course2.hashCode());
    }

    @Test
    void testToString() {
        User instructor = User.builder().id(1L).username("instructor").build();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0);

        Course course = Course.builder()
                .id(1L)
                .title("Java Course")
                .description("Learn Java")
                .category("Programming")
                .instructor(instructor)
                .startTime(startTime)
                .durationMinutes(120)
                .build();

        String toString = course.toString();
        
        assertTrue(toString.contains("Java Course"));
        assertTrue(toString.contains("Learn Java"));
        assertTrue(toString.contains("Programming"));
        assertTrue(toString.contains("120"));
        // instructor and enrolledUsers should be excluded from toString
        assertFalse(toString.contains("instructor"));
        assertFalse(toString.contains("enrolledUsers"));
    }

    @Test
    void testUserEnrollment() {
        Course course = new Course();
        User user1 = User.builder().id(1L).username("student1").build();
        User user2 = User.builder().id(2L).username("student2").build();

        course.getEnrolledUsers().add(user1);
        course.getEnrolledUsers().add(user2);

        assertEquals(2, course.getEnrolledUsers().size());
        assertTrue(course.getEnrolledUsers().contains(user1));
        assertTrue(course.getEnrolledUsers().contains(user2));
    }

    @Test
    void testInstructorRelationship() {
        User instructor = User.builder()
                .id(1L)
                .username("john_instructor")
                .role(Role.INSTRUCTOR)
                .build();

        Course course = Course.builder()
                .id(1L)
                .title("Advanced Java")
                .instructor(instructor)
                .build();

        assertEquals(instructor, course.getInstructor());
        assertEquals("john_instructor", course.getInstructor().getUsername());
        assertEquals(Role.INSTRUCTOR, course.getInstructor().getRole());
    }

    @Test
    void testStartTimeAndDuration() {
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 15, 14, 30);
        Course course = Course.builder()
                .startTime(startTime)
                .durationMinutes(90)
                .build();

        assertEquals(startTime, course.getStartTime());
        assertEquals(90, course.getDurationMinutes());
    }
} 