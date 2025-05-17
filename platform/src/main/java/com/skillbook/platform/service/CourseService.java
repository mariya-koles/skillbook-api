package com.skillbook.platform.service;

import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;


    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public void createCourse(CourseDto dto) {
        User instructor = userRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .startTime(dto.getStartTime())
                .durationMinutes(dto.getDurationMinutes())
                .instructor(instructor)
                .build();

        courseRepository.save(course);
    }
}
