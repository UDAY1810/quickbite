package com.quickbite.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.user_service.dto.AuthResponse;
import com.quickbite.user_service.dto.LoginRequest;
import com.quickbite.user_service.dto.RegisterRequest;
import com.quickbite.user_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/auth/register returns 201 Created and AuthResponse")
    void register_success() throws Exception {
        RegisterRequest req = new RegisterRequest("jane@quickbite.com", "password123", "Jane", "12345");
        AuthResponse resp = new AuthResponse("token-123", 1L, "Jane", "CUSTOMER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-123"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.fullName").value("Jane"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login returns 200 OK and AuthResponse")
    void login_success() throws Exception {
        LoginRequest req = new LoginRequest("jane@quickbite.com", "secret");
        AuthResponse resp = new AuthResponse("token-login", 1L, "Jane", "CUSTOMER");

        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-login"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(authService).login(any(LoginRequest.class));
    }
}
