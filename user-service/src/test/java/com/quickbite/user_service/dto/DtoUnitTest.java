package com.quickbite.user_service.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoUnitTest {

    @Test
    @DisplayName("RegisterRequest: constructor and accessors")
    void registerRequest_accessors() {
        RegisterRequest req = new RegisterRequest("user@quickbite.com", "password123", "John Doe", "9876543210");

        assertThat(req.email()).isEqualTo("user@quickbite.com");
        assertThat(req.password()).isEqualTo("password123");
        assertThat(req.fullName()).isEqualTo("John Doe");
        assertThat(req.phone()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("LoginRequest: constructor and accessors")
    void loginRequest_accessors() {
        LoginRequest req = new LoginRequest("user@quickbite.com", "password123");

        assertThat(req.email()).isEqualTo("user@quickbite.com");
        assertThat(req.password()).isEqualTo("password123");
    }

    @Test
    @DisplayName("AuthResponse: constructor and accessors")
    void authResponse_accessors() {
        AuthResponse res = new AuthResponse("jwt.mock.token", 10L, "John Doe", "CUSTOMER");

        assertThat(res.token()).isEqualTo("jwt.mock.token");
        assertThat(res.userId()).isEqualTo(10L);
        assertThat(res.fullName()).isEqualTo("John Doe");
        assertThat(res.role()).isEqualTo("CUSTOMER");
    }
}
