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

import java.time.LocalDateTime;
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
    void getAllCourses_shouldReturnEmptyList_whenNoCoursesExist() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseDto> result = courseService.getAllCourses();

        assertTrue(result.isEmpty());
        verify(courseRepository).findAll();
    }

    @Test
    void getAllCourses_shouldMapAllCourseFields() {
        User instructor = User.builder()
                .id(1L)
                .username("instructor")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        LocalDateTime startTime = LocalDateTime.of(2024, 6, 15, 10, 0);
        Course course = Course.builder()
                .id(1L)
                .title("Advanced Java")
                .description("Learn advanced Java concepts")
                .longDescription("A comprehensive course covering advanced Java topics")
                .category("Programming")
                .durationMinutes(120)
                .startTime(startTime)
                .instructor(instructor)
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<CourseDto> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        CourseDto dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("Advanced Java", dto.getTitle());
        assertEquals("Learn advanced Java concepts", dto.getDescription());
        assertEquals("A comprehensive course covering advanced Java topics", dto.getLongDescription());
        assertEquals("Programming", dto.getCategory());
        assertEquals(120, dto.getDurationMinutes());
        assertEquals(startTime, dto.getStartTime());
        assertEquals(1L, dto.getInstructorId());
        assertEquals("instructor", dto.getInstructor().getUsername());
        assertEquals("John", dto.getInstructor().getFirstName());
        assertEquals("Doe", dto.getInstructor().getLastName());
        assertEquals("john@example.com", dto.getInstructor().getEmail());
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

    @Test
    void createCourse_shouldSaveCourse_whenValidDto() {
        User instructor = User.builder()
                .id(1L)
                .username("instructor")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        CourseDto dto = CourseDto.builder()
                .title("New Course")
                .description("Course description")
                .longDescription("Long course description")
                .category("Programming")
                .durationMinutes(90)
                .startTime(LocalDateTime.now())
                .instructorId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));

        courseService.createCourse(dto);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(courseCaptor.capture());
        
        Course savedCourse = courseCaptor.getValue();
        assertEquals("New Course", savedCourse.getTitle());
        assertEquals("Course description", savedCourse.getDescription());
        assertEquals("Long course description", savedCourse.getLongDescription());
        assertEquals("Programming", savedCourse.getCategory());
        assertEquals(90, savedCourse.getDurationMinutes());
        assertEquals(instructor, savedCourse.getInstructor());
    }

    @Test
    void createCourse_shouldThrowException_whenInstructorNotFound() {
        CourseDto dto = CourseDto.builder()
                .title("New Course")
                .instructorId(999L)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.createCourse(dto));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void getCoursesByCategory_shouldReturnCourses_whenCategoryExists() {
        User instructor = User.builder()
                .id(1L)
                .username("instructor")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        Course course1 = Course.builder()
                .id(1L)
                .title("Java Basics")
                .category("Programming")
                .instructor(instructor)
                .build();
        
        Course course2 = Course.builder()
                .id(2L)
                .title("Advanced Java")
                .category("Programming")
                .instructor(instructor)
                .build();

        when(courseRepository.findByCategory("Programming"))
                .thenReturn(List.of(course1, course2));

        List<CourseDto> result = courseService.getCoursesByCategory("Programming");

        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Advanced Java", result.get(1).getTitle());
        verify(courseRepository).findByCategory("Programming");
    }

    @Test
    void getCoursesByCategory_shouldThrowException_whenNoCoursesfound() {
        when(courseRepository.findByCategory("NonExistentCategory"))
                .thenReturn(List.of());

        assertThrows(ResponseStatusException.class, 
                () -> courseService.getCoursesByCategory("NonExistentCategory"));
    }

    @Test
    void updateCourse_shouldUpdateAllFields_whenCourseExists() {
        Course existingCourse = Course.builder()
                .id(1L)
                .title("Old Title")
                .description("Old description")
                .build();

        CourseDto updateDto = CourseDto.builder()
                .title("Updated Title")
                .description("Updated description")
                .longDescription("Updated long description")
                .category("Updated Category")
                .durationMinutes(150)
                .startTime(LocalDateTime.of(2024, 7, 1, 14, 0))
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));

        courseService.updateCourse(1L, updateDto);

        assertEquals("Updated Title", existingCourse.getTitle());
        assertEquals("Updated description", existingCourse.getDescription());
        assertEquals("Updated long description", existingCourse.getLongDescription());
        assertEquals("Updated Category", existingCourse.getCategory());
        assertEquals(150, existingCourse.getDurationMinutes());
        assertEquals(LocalDateTime.of(2024, 7, 1, 14, 0), existingCourse.getStartTime());
        
        verify(courseRepository).save(existingCourse);
    }

    @Test
    void updateCourse_shouldThrowException_whenCourseNotFound() {
        CourseDto updateDto = CourseDto.builder()
                .title("Updated Title")
                .build();

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, 
                () -> courseService.updateCourse(999L, updateDto));
        
        verify(courseRepository, never()).save(any());
    }

    @Test
    void getAllCourses_shouldHandleNullInstructor() {
        Course courseWithoutInstructor = Course.builder()
                .id(1L)
                .title("Course Without Instructor")
                .instructor(null)
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(courseWithoutInstructor));

        assertThrows(NullPointerException.class, () -> courseService.getAllCourses());
    }

    @Test
    void getCourseById_shouldMapAllFields_whenCourseExists() {
        User instructor = User.builder()
                .id(1L)
                .username("instructor")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .build();

        LocalDateTime startTime = LocalDateTime.of(2024, 8, 15, 9, 30);
        Course course = Course.builder()
                .id(1L)
                .title("Complete Java Course")
                .description("Comprehensive Java training")
                .longDescription("Complete guide to Java programming")
                .category("Programming")
                .durationMinutes(240)
                .startTime(startTime)
                .instructor(instructor)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseDto result = courseService.getCourseById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Complete Java Course", result.getTitle());
        assertEquals("Comprehensive Java training", result.getDescription());
        assertEquals("Complete guide to Java programming", result.getLongDescription());
        assertEquals("Programming", result.getCategory());
        assertEquals(240, result.getDurationMinutes());
        assertEquals(startTime, result.getStartTime());
        assertEquals(1L, result.getInstructorId());
        assertNotNull(result.getInstructor());
        assertEquals("instructor", result.getInstructor().getUsername());
        assertEquals("Jane", result.getInstructor().getFirstName());
        assertEquals("Smith", result.getInstructor().getLastName());
        assertEquals("jane@example.com", result.getInstructor().getEmail());
    }
}
