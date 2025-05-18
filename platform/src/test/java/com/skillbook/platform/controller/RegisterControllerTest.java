package com.skillbook.platform.controller;

import com.skillbook.platform.model.User;
import com.skillbook.platform.repository.UserRepository;
import com.skillbook.platform.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RegisterControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    @Test
    public void whenValidUser_thenReturnsSuccess() {
        // given
        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .role(Role.LEARNER)
                .build();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ResponseEntity<?> response = registerController.processRegistration(user);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("User registered successfully");
    }

    @Test
    public void whenUserExists_thenReturnsBadRequest() {
        // given
        User existingUser = User.builder()
                .username("existing")
                .email("existing@example.com")
                .password("password123")
                .role(Role.LEARNER)
                .build();

        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        // when
        ResponseEntity<?> response = registerController.processRegistration(existingUser);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("Username already exists.");
    }
} 