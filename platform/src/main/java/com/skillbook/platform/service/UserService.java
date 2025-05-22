package com.skillbook.platform.service;

import com.skillbook.platform.dto.InstructorDto;
import com.skillbook.platform.dto.UserDto;
import com.skillbook.platform.dto.CourseDto;
import com.skillbook.platform.model.Course;
import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.CourseRepository;
import com.skillbook.platform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    public UserDto findByUsername(String username) {
        User user =  userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .enrolledCourses(
                        user.getEnrolledCourses().stream()
                                .map(course -> CourseDto.builder()
                                        .id(course.getId())
                                        .title(course.getTitle())
                                        .description(course.getDescription())
                                        .instructor(
                                                course.getInstructor() != null
                                                        ? InstructorDto.builder()
                                                        .id(course.getInstructor().getId())
                                                        .firstName(course.getInstructor().getFirstName())
                                                        .lastName(course.getInstructor().getLastName())
                                                        .build()
                                                        : null
                                        )
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    public void createUser(UserDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .build();
        userRepository.save(user);
    }

    public void updateUser(UserDto dto) {
        User existing = userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        existing.setEmail(dto.getEmail());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setRole(dto.getRole());
        existing.setProfilepic(dto.getProfilepic());
        Set<Course> enrolledCourses = dto.getEnrolledCourses().stream()
                .map(courseDto -> courseRepository.findById(courseDto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseDto.getId())))
                .collect(Collectors.toSet());

        existing.setEnrolledCourses(enrolledCourses);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(dto.getPassword()); // encode if needed
        }

        userRepository.save(existing);
    }
}
