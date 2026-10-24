package com.quickbite.user_service.service;

import com.quickbite.user_service.dto.AuthResponse;
import com.quickbite.user_service.dto.LoginRequest;
import com.quickbite.user_service.dto.RegisterRequest;
import com.quickbite.user_service.entity.Role;
import com.quickbite.user_service.entity.User;
import com.quickbite.user_service.exception.ConflictException;
import com.quickbite.user_service.exception.UnauthorizedException;
import com.quickbite.user_service.repository.UserRepository;
import com.quickbite.user_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(repo, encoder, jwt);
    }

    @Test
    @DisplayName("register: saves new user, encodes password and returns token")
    void register_success() {
        RegisterRequest req = new RegisterRequest("test@quickbite.com", "pass123", "John Doe", "9876543210");
        when(repo.existsByEmail("test@quickbite.com")).thenReturn(false);
        when(encoder.encode("pass123")).thenReturn("encodedPass");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setEmail(req.email());
        savedUser.setPassword("encodedPass");
        savedUser.setFullName(req.fullName());
        savedUser.setPhone(req.phone());
        savedUser.setRole(Role.CUSTOMER);

        when(repo.save(any(User.class))).thenReturn(savedUser);
        when(jwt.generateToken(10L, "CUSTOMER")).thenReturn("mock-jwt-token");

        AuthResponse resp = authService.register(req);

        assertThat(resp).isNotNull();
        assertThat(resp.token()).isEqualTo("mock-jwt-token");
        assertThat(resp.userId()).isEqualTo(10L);
        assertThat(resp.fullName()).isEqualTo("John Doe");
        assertThat(resp.role()).isEqualTo("CUSTOMER");

        verify(repo).existsByEmail("test@quickbite.com");
        verify(encoder).encode("pass123");
        verify(repo).save(any(User.class));
        verify(jwt).generateToken(10L, "CUSTOMER");
    }

    @Test
    @DisplayName("register: throws ConflictException when email already exists")
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest req = new RegisterRequest("existing@quickbite.com", "pass123", "John", "12345");
        when(repo.existsByEmail("existing@quickbite.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already registered");

        verify(repo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login: returns token on valid credentials")
    void login_success() {
        LoginRequest req = new LoginRequest("john@quickbite.com", "mypass");
        User user = new User();
        user.setId(5L);
        user.setEmail("john@quickbite.com");
        user.setPassword("hashedPass");
        user.setFullName("John Doe");
        user.setRole(Role.CUSTOMER);

        when(repo.findByEmail("john@quickbite.com")).thenReturn(Optional.of(user));
        when(encoder.matches("mypass", "hashedPass")).thenReturn(true);
        when(jwt.generateToken(5L, "CUSTOMER")).thenReturn("jwt-john-5");

        AuthResponse resp = authService.login(req);

        assertThat(resp.token()).isEqualTo("jwt-john-5");
        assertThat(resp.userId()).isEqualTo(5L);
        assertThat(resp.fullName()).isEqualTo("John Doe");
        assertThat(resp.role()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("login: throws UnauthorizedException when user does not exist")
    void login_userNotFound_throwsUnauthorized() {
        LoginRequest req = new LoginRequest("unknown@quickbite.com", "pass");
        when(repo.findByEmail("unknown@quickbite.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Bad credentials");
    }

    @Test
    @DisplayName("login: throws UnauthorizedException when password does not match")
    void login_wrongPassword_throwsUnauthorized() {
        LoginRequest req = new LoginRequest("john@quickbite.com", "wrongpass");
        User user = new User();
        user.setEmail("john@quickbite.com");
        user.setPassword("correctHash");

        when(repo.findByEmail("john@quickbite.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrongpass", "correctHash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Bad credentials");
    }
}
