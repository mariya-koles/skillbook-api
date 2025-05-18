package com.skillbook.platform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbook.platform.model.User;
import com.skillbook.platform.enums.Role;
import com.skillbook.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class RegistrationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSuccessfulRegistration() throws Exception {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .role(Role.LEARNER)
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        // Verify user was saved with correct data
        User savedUser = userRepository.findByUsername("testuser")
                .orElseThrow(() -> new AssertionError("User not found"));
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getFirstName()).isEqualTo("Test");
        assertThat(savedUser.getLastName()).isEqualTo("User");
        assertThat(savedUser.getRole()).isEqualTo(Role.LEARNER);
    }

    @Test
    public void testDuplicateUsername() throws Exception {
        // Create initial user
        User existingUser = User.builder()
                .username("existing")
                .email("existing@example.com")
                .password("password123")
                .firstName("Existing")
                .lastName("User")
                .role(Role.LEARNER)
                .build();
        userRepository.save(existingUser);

        // Try to register with same username
        User duplicateUser = User.builder()
                .username("existing")
                .email("another@example.com")
                .password("different123")
                .firstName("Another")
                .lastName("User")
                .role(Role.LEARNER)
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists."));
    }
} 