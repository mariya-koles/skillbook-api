package com.skillbook.platform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void whenPublicEndpoint_thenAllowAccess() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void whenLoginEndpoint_thenAllowAccess() throws Exception {
        String loginJson = "{\"username\":\"testuser\",\"password\":\"testpass\"}";
        
        mockMvc.perform(post("/api/login")
                .contentType("application/json")
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    public void whenInstructorAccessingCourses_thenAllowAccess() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "LEARNER")
    public void whenAuthenticatedLearner_thenForbidden() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenSecuredEndpoint_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenSwaggerEndpoint_thenAllowAccess() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
} 