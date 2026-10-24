package com.quickbite.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.user_service.dto.LoginRequest;
import com.quickbite.user_service.dto.RegisterRequest;
import com.quickbite.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Register -> Login flow with DB and JWT")
    void fullRegistrationAndLoginFlow() throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                "integration@quickbite.com", "Password@123", "Integration User", "9998887770"
        );

        // 1. Register new user
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Integration User"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        assertThat(userRepository.existsByEmail("integration@quickbite.com")).isTrue();

        // 2. Duplicate registration should return 409 Conflict
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));

        // 3. Login with correct password should return 200 OK & JWT
        LoginRequest loginReq = new LoginRequest("integration@quickbite.com", "Password@123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Integration User"));

        // 4. Login with bad password should return 401 Unauthorized
        LoginRequest badLoginReq = new LoginRequest("integration@quickbite.com", "WrongPassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLoginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    @DisplayName("Integration: Validation fails on invalid register request (empty fields)")
    void register_validationFailure() throws Exception {
        RegisterRequest invalidReq = new RegisterRequest("", "", "", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }
}
