package com.skillbook.platform.service;

import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_shouldReturnListOfCourseDtos() {
        User instructor = User.builder().id(11L).firstName("Alice").lastName("Smith").email("alice@example.com").build();
        Course course = Course.builder().id(1L).title("Java 101").instructor(instructor).build();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseDto> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("Java 101", result.get(0).getTitle());
    }

    @Test
    void getCourseById_shouldReturnCourseDto_whenCourseExists() {
        User instructor = User.builder().id(22L).firstName("Bob").lastName("Jones").email("bob@example.com").build();
        Course course = Course.builder().id(1L).title("Spring Boot").instructor(instructor).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDto result = courseService.getCourseById(1L);

        assertEquals("Spring Boot", result.getTitle());
    }

    @Test
    void getCourseById_shouldThrowException_whenCourseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> courseService.getCourseById(99L));
    }
}
