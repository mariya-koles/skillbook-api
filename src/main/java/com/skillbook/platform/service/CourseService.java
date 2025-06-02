package com.skillbook.platform.service;

import com.skillbook.platform.controller.CourseController;
import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.dto.InstructorDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(course -> CourseDto.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .description(course.getDescription())
                        .longDescription(course.getLongDescription())
                        .category(course.getCategory())
                        .durationMinutes(course.getDurationMinutes())
                        .startTime(course.getStartTime())
                        .instructorId(course.getInstructor().getId())
                        .instructor(
                            course.getInstructor() != null
                                ? InstructorDto.builder()
                                    .id(course.getInstructor().getId())
                                    .username(course.getInstructor().getUsername())
                                    .firstName(course.getInstructor().getFirstName())
                                    .lastName(course.getInstructor().getLastName())
                                    .email(course.getInstructor().getEmail())
                                    .build()
                                : null
                        )
                        .build())
                .toList();
    }

    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found"));

        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .longDescription(course.getLongDescription())
                .category(course.getCategory())
                .durationMinutes(course.getDurationMinutes())
                .startTime(course.getStartTime())
                .instructorId(course.getInstructor().getId())
                .instructor(
                    course.getInstructor() != null
                        ? InstructorDto.builder()
                            .id(course.getInstructor().getId())
                            .username(course.getInstructor().getUsername())
                            .firstName(course.getInstructor().getFirstName())
                            .lastName(course.getInstructor().getLastName())
                            .email(course.getInstructor().getEmail())
                            .build()
                        : null
                )
                .build();
    }

    public void createCourse(CourseDto dto) {
        User instructor = userRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .longDescription(dto.getLongDescription())
                .category(dto.getCategory())
                .startTime(dto.getStartTime())
                .durationMinutes(dto.getDurationMinutes())
                .instructor(instructor)
                .build();

        courseRepository.save(course);
    }

    public List<CourseDto> getCoursesByCategory(String category) {
        List<Course> courses = courseRepository.findByCategory(category);
        if (courses.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No courses found in that category");
        }
        return courses.stream()
                .map(course -> CourseDto.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .description(course.getDescription())
                        .longDescription(course.getLongDescription())
                        .category(course.getCategory())
                        .durationMinutes(course.getDurationMinutes())
                        .startTime(course.getStartTime())
                        .instructorId(course.getInstructor().getId())
                        .instructor(
                            course.getInstructor() != null
                                ? InstructorDto.builder()
                                    .id(course.getInstructor().getId())
                                    .username(course.getInstructor().getUsername())
                                    .firstName(course.getInstructor().getFirstName())
                                    .lastName(course.getInstructor().getLastName())
                                    .email(course.getInstructor().getEmail())
                                    .build()
                                : null
                        )
                        .build())
                .toList();
    }

    public void updateCourse(Long courseId, CourseDto dto) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found"));

        existingCourse.setTitle(dto.getTitle());
        existingCourse.setDescription(dto.getDescription());
        existingCourse.setLongDescription(dto.getLongDescription());
        existingCourse.setCategory(dto.getCategory());
        existingCourse.setDurationMinutes(dto.getDurationMinutes());
        existingCourse.setStartTime(dto.getStartTime());

        courseRepository.save(existingCourse);
    }



}
