package com.skillbook.platform.service;

import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.enums.Role;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUsername_shouldReturnUserDto_whenUserExists() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LEARNER)
                .enrolledCourses(new HashSet<>())
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDto result = userService.findByUsername("john");

        assertEquals("john", result.getUsername());
        verify(userRepository).findByUsername("john");
    }

    @Test
    void findByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findByUsername("missing"));
    }

    @Test
    void updateUser_shouldUpdateFieldsAndCourses() {
        Course course = Course.builder().id(100L).title("Java 101").build();
        CourseDto courseDto = CourseDto.builder().id(100L).build();

        User existing = User.builder().id(1L).username("john").enrolledCourses(new HashSet<>()).build();
        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .role(Role.LEARNER)
                .profilepic("pic.jpg".getBytes())
                .enrolledCourses(List.of(courseDto))
                .password("newpass")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));

        userService.updateUser(incoming);

        assertEquals("Updated", existing.getFirstName());
        assertTrue(existing.getEnrolledCourses().contains(course));
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_shouldThrowException_whenCourseNotFound() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .enrolledCourses(List.of(CourseDto.builder().id(999L).build()))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(dto));
    }
}
