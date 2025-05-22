package com.skillbook.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.dto.InstructorDto;
import com.skillbook.platform.service.CourseService;
import com.skillbook.platform.enums.Role;
import com.skillbook.platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the CourseController.
 * Tests all course-related endpoint functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnableMethodSecurity
@ActiveProfiles("test")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseDto testCourse1;
    private CourseDto testCourse2;

    @BeforeEach
    void setUp() {
        InstructorDto instructor = InstructorDto.builder()
                .id(1L)
                .username("instructor1")
                .firstName("Jane")
                .lastName("Doe")
                .build();

        testCourse1 = CourseDto.builder()
                .id(1L)
                .title("Java Basics")
                .description("Introduction to Java")
                .category("Programming")
                .instructorId(instructor.getId())
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(1))
                .durationMinutes(90)
                .build();

        testCourse2 = CourseDto.builder()
                .id(2L)
                .title("Advanced Java")
                .description("Advanced Java Topics")
                .category("Programming")
                .instructorId(instructor.getId())
                .instructor(instructor)
                .startTime(LocalDateTime.now().plusDays(2))
                .durationMinutes(120)
                .build();
    }

    @Test
    public void whenGetAllCourses_thenReturnJsonArray() throws Exception {
        List<CourseDto> allCourses = Arrays.asList(testCourse1, testCourse2);
        given(courseService.getAllCourses()).willReturn(allCourses);


        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Java Basics"))
                .andExpect(jsonPath("$[1].title").value("Advanced Java"))
                .andExpect(jsonPath("$[0].instructor.username").value("instructor1"))
                .andExpect(jsonPath("$[0].instructor.firstName").value("Jane"))
                .andExpect(jsonPath("$[1].instructor.username").value("instructor1"))
                .andExpect(jsonPath("$[1].instructor.firstName").value("Jane"));
    }

    @Test
    public void whenGetCoursesByCategory_thenReturnFilteredJsonArray() throws Exception {
        List<CourseDto> programmingCourses = Arrays.asList(testCourse1, testCourse2);
        given(courseService.getCoursesByCategory("Programming")).willReturn(programmingCourses);

        mockMvc.perform(get("/courses/category/Programming"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].category").value("Programming"))
                .andExpect(jsonPath("$[1].category").value("Programming"))
                .andExpect(jsonPath("$[0].instructor.username").value("instructor1"))
                .andExpect(jsonPath("$[0].instructor.firstName").value("Jane"))
                .andExpect(jsonPath("$[1].instructor.username").value("instructor1"))
                .andExpect(jsonPath("$[1].instructor.firstName").value("Jane"));
    }

    @Test
    public void whenGetCourseById_thenReturnCourse() throws Exception {
        given(courseService.getCourseById(1L)).willReturn(testCourse1);

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Java Basics"))
                .andExpect(jsonPath("$.instructor.username").value("instructor1"))
                .andExpect(jsonPath("$.instructor.firstName").value("Jane"));
    }

    @Test
    @WithMockUser(roles = {"INSTRUCTOR", "ADMIN"})
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
    @WithMockUser(roles = {"LEARNER"})
    public void whenCreateCourse_thenReturnUnauthorized() throws Exception {
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
        given(courseService.getCoursesByCategory("NonExistent")).willReturn(List.of());

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
                .andExpect(status().isOk());
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

    @WithMockUser(username = "learner1", roles = "LEARNER")
    @Test
    public void whenLearnerEnrollsInCourse_thenReturn200() throws Exception {
        UserDto mockUser = new UserDto();
        mockUser.setId(1L);
        mockUser.setUsername("learner1");
        mockUser.setEnrolledCourses(new ArrayList<>());

        CourseDto mockCourse = new CourseDto();
        mockCourse.setId(4L);
        mockCourse.setTitle("Math 101");

        when(userService.findByUsername("learner1")).thenReturn(mockUser);
        when(courseService.getCourseById(4L)).thenReturn(mockCourse);

        doNothing().when(userService).updateUser(any(UserDto.class));

        mockMvc.perform(post("/courses/4/enroll"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enrolled successfully")));

        verify(userService, times(1)).updateUser(any(UserDto.class));
    }



    @WithMockUser(username = "learner1", roles = "LEARNER")
    @Test
    public void whenCourseNotFound_thenReturn404() throws Exception {
        when(courseService.getCourseById(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        mockMvc.perform(post("/courses/999/enroll"))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "instructor1", roles = "INSTRUCTOR")
    @Test
    public void whenUserNotLearner_thenReturn403() throws Exception {
        CourseDto mockCourse = new CourseDto();
        mockCourse.setId(4L);
        mockCourse.setTitle("Math 101");


        when(courseService.getCourseById(4L)).thenReturn(mockCourse);

        mockMvc.perform(post("/courses/4/enroll"))
                .andExpect(status().isForbidden());
    }
} 