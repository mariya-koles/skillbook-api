package com.skillbook.platform.controller;

import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.service.CourseService;
import com.skillbook.platform.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing courses.
 * Provides endpoints for course creation, retrieval, and management.
 *
 * @author mariya-koles
 * @version 1.0
 * @since 2025-03
 */
@RestController
@RequestMapping("/courses")
public class CourseController {


    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    private final UserService userService;

    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    /**
     * Retrieves all courses in the system.
     *
     * @return ResponseEntity containing a list of all courses
     * @HTTP 200 OK with the list of courses
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        log.info("Fetching courses...");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Retrieves courses filtered by category.
     *
     * @param category the category to filter courses by
     * @return ResponseEntity containing a list of courses in the specified category
     * @HTTP 200 OK with the filtered list of courses
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<CourseDto>> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(category));

    }

    /**
     * Retrieves a specific course by its ID.
     *
     * @param id the ID of the course to retrieve
     * @return the requested Course object
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        try {
            CourseDto course = courseService.getCourseById(id);
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
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseDto dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty() ||
                dto.getDescription() == null || dto.getDescription().trim().isEmpty() ||
                dto.getCategory() == null || dto.getCategory().trim().isEmpty() ||
                dto.getStartTime() == null ||
                dto.getDurationMinutes() <= 0) {
            return ResponseEntity.badRequest()
                    .body("All fields are required and must be valid");
        }

        try {
            courseService.createCourse(dto);
            return ResponseEntity.ok("Course created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Enrolls the currently authenticated user in the specified course.
     *
     * @param courseId       the ID of the course to enroll in
     * @param authentication the Spring Security authentication object containing the current user's
     *                       identity
     * @return ResponseEntity indicating success or failure of the enrollment
     */
    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('LEARNER')")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId,
                                          Authentication authentication) {
        // gets the authenticated username
        String username = authentication.getName();

        UserDto user = userService.findByUsername(username);
        CourseDto course = courseService.getCourseById(courseId);


        if (user.getEnrolledCourses() == null) {
            user.setEnrolledCourses(new ArrayList<>());
        }
        user.getEnrolledCourses().add(course);
        userService.updateUser(user);  // persist the enrollment

        return ResponseEntity.ok("Enrolled successfully in course ID " + courseId);
    }


}
