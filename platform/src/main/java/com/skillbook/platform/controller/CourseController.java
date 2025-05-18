package com.skillbook.platform.controller;

import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.service.CourseService;
import com.skillbook.platform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST controller for managing courses.
 * Provides endpoints for course creation, retrieval, and management.
 *
 * @author Skillbook Team
 * @version 1.0
 * @since 2024-03
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;
    @Autowired
    private CourseRepository courseRepository;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Retrieves all courses in the system.
     *
     * @return ResponseEntity containing a list of all courses
     * @HTTP 200 OK with the list of courses
     */
    @GetMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    /**
     * Retrieves courses filtered by category.
     *
     * @param category the category to filter courses by
     * @return ResponseEntity containing a list of courses in the specified category
     * @HTTP 200 OK with the filtered list of courses
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(courseRepository.findByCategory(category));
    }

    /**
     * Retrieves a specific course by its ID.
     *
     * @param id the ID of the course to retrieve
     * @return the requested Course object
     * @throws com.skillbook.platform.exception.ResourceNotFoundException if course not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.getCourseById(id);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new course.
     *
     * @param dto the course data transfer object containing course details
     * @return ResponseEntity with creation status message
     * @HTTP 200 OK if course creation is successful
     */
    @PostMapping("/courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
            dto.getDescription() == null || dto.getDescription().trim().isEmpty() ||
            dto.getCategory() == null || dto.getCategory().trim().isEmpty() ||
            dto.getStartTime() == null ||
            dto.getDurationMinutes() <= 0) {
            return ResponseEntity.badRequest().body("All fields are required and must be valid");
        }

        try {
            courseService.createCourse(dto);
            return ResponseEntity.ok("Course created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
