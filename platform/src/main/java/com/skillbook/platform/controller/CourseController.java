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

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(courseRepository.findByCategory(category));
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody CourseDto dto) {
        courseService.createCourse(dto);
        return ResponseEntity.ok("Course created successfully.");
    }
}
