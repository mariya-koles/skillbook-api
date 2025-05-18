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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RegistrationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSuccessfulRegistrationFlow() throws Exception {
        // Create a test user
        User user = User.builder()
                .username("integrationtest")
                .email("integration@test.com")
                .password("testpass123")
                .role(Role.LEARNER)
                .build();

        // Register the user
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        // Verify user exists in database
        User savedUser = userRepository.findByUsername("integrationtest").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("integration@test.com");
        assertThat(savedUser.getRole()).isEqualTo(Role.LEARNER);

        // Try to register same user again
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists."));
    }

    @Test
    public void testRegistrationValidation() throws Exception {
        // Test with missing required fields
        User invalidUser = User.builder()
                .username("") // empty username
                .email("invalid@test.com")
                .password("pass123")
                .role(Role.LEARNER)
                .build();

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        // Verify user was not saved
        assertThat(userRepository.findByUsername("")).isEmpty();
    }
} 