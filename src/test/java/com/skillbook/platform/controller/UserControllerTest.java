package com.skillbook.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.dto.InstructorDto;
import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.enums.Role;
import com.skillbook.platform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserDto mockUser;

    @BeforeEach
    void setUp() {
        InstructorDto instructor = InstructorDto.builder()
                .id(101L)
                .firstName("Jane")
                .lastName("Doe")
                .build();

        CourseDto course = CourseDto.builder()
                .id(1L)
                .title("Java 101")
                .description("Intro to Java")
                .instructor(instructor)
                .build();

        mockUser = UserDto.builder()
                .id(42L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.LEARNER)
                .enrolledCourses(List.of(course))
                .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldReturnCurrentUserProfile() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(mockUser);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.enrolledCourses[0].title").value("Java 101"));

        verify(userService).findByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldUpdateUserProfile() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(mockUser);
        doNothing().when(userService).updateUser(any(UserDto.class));

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstName").value("Test"));

        verify(userService).updateUser(any(UserDto.class));
        verify(userService, times(2)).findByUsername("testuser"); // once before, once after
    }
}
