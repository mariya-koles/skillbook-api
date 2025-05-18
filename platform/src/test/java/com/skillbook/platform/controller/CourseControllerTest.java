package com.skillbook.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.service.CourseService;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.enums.Role;
import com.skillbook.platform.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the CourseController.
 * Tests all course-related endpoint functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Course testCourse1;
    private Course testCourse2;
    private User instructor;

    @BeforeEach
    void setUp() {
        instructor = User.builder()
                .id(1L)
                .username("instructor1")
                .email("instructor@test.com")
                .password("password123")
                .role(Role.INSTRUCTOR)
                .build();

        testCourse1 = Course.builder()
                .id(1L)
                .title("Java Basics")
                .description("Introduction to Java")
                .category("Programming")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(90)
                .build();

        testCourse2 = Course.builder()
                .id(2L)
                .title("Advanced Java")
                .description("Advanced Java Topics")
                .category("Programming")
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(2))
                .durationMinutes(120)
                .build();
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenGetAllCourses_thenReturnJsonArray() throws Exception {
        List<Course> allCourses = Arrays.asList(testCourse1, testCourse2);
        given(courseRepository.findAll()).willReturn(allCourses);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Java Basics"))
                .andExpect(jsonPath("$[1].title").value("Advanced Java"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenGetCoursesByCategory_thenReturnFilteredJsonArray() throws Exception {
        List<Course> programmingCourses = Arrays.asList(testCourse1, testCourse2);
        given(courseRepository.findByCategory("Programming")).willReturn(programmingCourses);

        mockMvc.perform(get("/courses/category/Programming"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].category").value("Programming"))
                .andExpect(jsonPath("$[1].category").value("Programming"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenGetCourseById_thenReturnCourse() throws Exception {
        given(courseService.getCourseById(1L)).willReturn(testCourse1);

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Basics"));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenCreateCourse_thenReturnSuccess() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("New Course");
        courseDto.setDescription("Test Description");
        courseDto.setCategory("Test Category");
        courseDto.setStartTime(LocalDateTime.now().plusDays(1));
        courseDto.setDurationMinutes(60);

        mockMvc.perform(post("/courses/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Course created successfully."));

        verify(courseService).createCourse(any(CourseDto.class));
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenGetNonExistentCourse_thenReturn404() throws Exception {
        given(courseService.getCourseById(999L))
                .willThrow(new RuntimeException("Course not found"));

        mockMvc.perform(get("/courses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenGetCoursesByNonExistentCategory_thenReturnEmptyArray() throws Exception {
        given(courseRepository.findByCategory("NonExistent")).willReturn(List.of());

        mockMvc.perform(get("/courses/category/NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenCreateInvalidCourse_thenReturn400() throws Exception {
        CourseDto invalidCourse = new CourseDto();
        // Not setting required fields

        mockMvc.perform(post("/courses/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCourse)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenUnauthorizedUser_thenReturn401() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "LEARNER")
    public void whenWrongRole_thenReturn403() throws Exception {
        CourseDto courseDto = new CourseDto();
        courseDto.setTitle("New Course");
        courseDto.setDescription("Test Description");
        courseDto.setCategory("Test Category");
        courseDto.setStartTime(LocalDateTime.now().plusDays(1));
        courseDto.setDurationMinutes(60);

        mockMvc.perform(post("/courses/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isForbidden());
    }
} 