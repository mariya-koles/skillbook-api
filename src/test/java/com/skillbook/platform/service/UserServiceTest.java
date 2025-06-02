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
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnUserDto_whenUserExists() {
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LEARNER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Role.LEARNER, result.getRole());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findById(999L));
        verify(userRepository).findById(999L);
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
    void findByUsername_shouldReturnUserWithCourses_whenUserHasEnrolledCourses() {
        User instructor = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .build();

        Course course = Course.builder()
                .id(100L)
                .title("Java 101")
                .description("Basic Java course")
                .instructor(instructor)
                .build();

        Set<Course> enrolledCourses = new HashSet<>();
        enrolledCourses.add(course);

        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.LEARNER)
                .profilePhoto("photo".getBytes())
                .enrolledCourses(enrolledCourses)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDto result = userService.findByUsername("john");

        assertEquals("john", result.getUsername());
        assertArrayEquals("photo".getBytes(), result.getProfilePhoto());
        assertEquals(1, result.getEnrolledCourses().size());
        CourseDto courseDto = result.getEnrolledCourses().get(0);
        assertEquals(100L, courseDto.getId());
        assertEquals("Java 101", courseDto.getTitle());
        assertEquals("Basic Java course", courseDto.getDescription());
        assertNotNull(courseDto.getInstructor());
        assertEquals("Jane", courseDto.getInstructor().getFirstName());
        assertEquals("Smith", courseDto.getInstructor().getLastName());
    }

    @Test
    void findByUsername_shouldHandleNullInstructor_inEnrolledCourses() {
        Course courseWithoutInstructor = Course.builder()
                .id(100L)
                .title("Self-paced Course")
                .description("Course without instructor")
                .instructor(null)
                .build();

        Set<Course> enrolledCourses = new HashSet<>();
        enrolledCourses.add(courseWithoutInstructor);

        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .enrolledCourses(enrolledCourses)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDto result = userService.findByUsername("john");

        assertEquals(1, result.getEnrolledCourses().size());
        CourseDto courseDto = result.getEnrolledCourses().get(0);
        assertEquals("Self-paced Course", courseDto.getTitle());
        assertNull(courseDto.getInstructor());
    }

    @Test
    void findByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findByUsername("missing"));
    }

    @Test
    void createUser_shouldSaveUser_whenValidDto() {
        UserDto dto = UserDto.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .role(Role.LEARNER)
                .build();

        userService.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("password123", savedUser.getPassword());
        assertEquals("New", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
        assertEquals(Role.LEARNER, savedUser.getRole());
    }

    @Test
    void updateUser_shouldUpdateAllFields_whenValidDto() {
        Course course = Course.builder().id(100L).title("Java 101").build();
        CourseDto courseDto = CourseDto.builder().id(100L).build();

        User existing = User.builder()
                .id(1L)
                .username("john")
                .email("old@example.com")
                .firstName("OldFirst")
                .lastName("OldLast")
                .role(Role.LEARNER)
                .enrolledCourses(new HashSet<>())
                .build();

        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("User")
                .email("updated@example.com")
                .password("newpass")
                .role(Role.INSTRUCTOR)
                .profilePhoto("pic.jpg".getBytes())
                .enrolledCourses(List.of(courseDto))
                .build();

        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course));

        userService.updateUser(incoming);

        assertEquals("updated@example.com", existing.getEmail());
        assertEquals("Updated", existing.getFirstName());
        assertEquals("User", existing.getLastName());
        assertEquals("encodedpass", existing.getPassword());
        assertEquals(Role.INSTRUCTOR, existing.getRole());
        assertArrayEquals("pic.jpg".getBytes(), existing.getProfilePhoto());
        assertTrue(existing.getEnrolledCourses().contains(course));
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_shouldNotUpdatePassword_whenPasswordIsNull() {
        User existing = User.builder()
                .id(1L)
                .username("john")
                .password("oldpassword")
                .enrolledCourses(new HashSet<>())
                .build();

        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .password(null)
                .enrolledCourses(List.of())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.updateUser(incoming);

        assertEquals("oldpassword", existing.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_shouldNotUpdatePassword_whenPasswordIsBlank() {
        User existing = User.builder()
                .id(1L)
                .username("john")
                .password("oldpassword")
                .enrolledCourses(new HashSet<>())
                .build();

        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .password("   ")
                .enrolledCourses(List.of())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.updateUser(incoming);

        assertEquals("oldpassword", existing.getPassword());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        UserDto dto = UserDto.builder()
                .id(999L)
                .firstName("Updated")
                .enrolledCourses(List.of())
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenCourseNotFound() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .enrolledCourses(List.of(CourseDto.builder().id(999L).build()))
                .build();
        when(passwordEncoder.encode(any())).thenReturn("doesntmatter");
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(dto));
    }

    @Test
    void updateUser_shouldHandleMultipleCourses() {
        Course course1 = Course.builder().id(100L).title("Java 101").build();
        Course course2 = Course.builder().id(200L).title("Spring Boot").build();
        
        CourseDto courseDto1 = CourseDto.builder().id(100L).build();
        CourseDto courseDto2 = CourseDto.builder().id(200L).build();

        User existing = User.builder()
                .id(1L)
                .username("john")
                .enrolledCourses(new HashSet<>())
                .build();

        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .enrolledCourses(List.of(courseDto1, courseDto2))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(100L)).thenReturn(Optional.of(course1));
        when(courseRepository.findById(200L)).thenReturn(Optional.of(course2));

        userService.updateUser(incoming);

        assertEquals(2, existing.getEnrolledCourses().size());
        assertTrue(existing.getEnrolledCourses().contains(course1));
        assertTrue(existing.getEnrolledCourses().contains(course2));
    }

    @Test
    void updateUser_shouldClearEnrolledCourses_whenEmptyList() {
        Course existingCourse = Course.builder().id(100L).title("Java 101").build();
        Set<Course> enrolledCourses = new HashSet<>();
        enrolledCourses.add(existingCourse);

        User existing = User.builder()
                .id(1L)
                .username("john")
                .enrolledCourses(enrolledCourses)
                .build();

        UserDto incoming = UserDto.builder()
                .id(1L)
                .firstName("Updated")
                .enrolledCourses(List.of())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.updateUser(incoming);

        assertTrue(existing.getEnrolledCourses().isEmpty());
    }
}
