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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        RegisterRequest req = new RegisterRequest("test@quickbite.com", "pass12345", "John Doe", "9876543210");
        when(repo.existsByEmail("test@quickbite.com")).thenReturn(false);
        when(encoder.encode("pass12345")).thenReturn("encodedPass");

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
        verify(encoder).encode("pass12345");
        verify(repo).save(any(User.class));
        verify(jwt).generateToken(10L, "CUSTOMER");
    }

    @Test
    @DisplayName("register: verifies exact User entity fields passed to repository with ArgumentCaptor")
    void register_verifiesExactFieldsSavedWithArgumentCaptor() {
        RegisterRequest req = new RegisterRequest("alice@quickbite.com", "securePassword99", "Alice Smith", "1122334455");
        when(repo.existsByEmail("alice@quickbite.com")).thenReturn(false);
        when(encoder.encode("securePassword99")).thenReturn("argon2$aliceEncoded");

        User savedUser = new User();
        savedUser.setId(25L);
        savedUser.setEmail(req.email());
        savedUser.setPassword("argon2$aliceEncoded");
        savedUser.setFullName(req.fullName());
        savedUser.setPhone(req.phone());
        savedUser.setRole(Role.CUSTOMER);

        when(repo.save(any(User.class))).thenReturn(savedUser);
        when(jwt.generateToken(25L, "CUSTOMER")).thenReturn("token-alice-25");

        authService.register(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo("alice@quickbite.com");
        assertThat(captured.getPassword()).isEqualTo("argon2$aliceEncoded");
        assertThat(captured.getFullName()).isEqualTo("Alice Smith");
        assertThat(captured.getPhone()).isEqualTo("1122334455");
        assertThat(captured.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("register: throws ConflictException when email already exists")
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest req = new RegisterRequest("existing@quickbite.com", "pass12345", "John", "12345");
        when(repo.existsByEmail("existing@quickbite.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already registered");

        verify(repo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login: returns token on valid credentials for CUSTOMER")
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
    @DisplayName("login: returns matching role in token when user is RESTAURANT_OWNER")
    void login_asRestaurantOwner_returnsOwnerRole() {
        LoginRequest req = new LoginRequest("owner@bistro.com", "ownerSecret123");
        User owner = new User();
        owner.setId(99L);
        owner.setEmail("owner@bistro.com");
        owner.setPassword("hashedOwnerSecret");
        owner.setFullName("Chef Mario");
        owner.setRole(Role.RESTAURANT_OWNER);

        when(repo.findByEmail("owner@bistro.com")).thenReturn(Optional.of(owner));
        when(encoder.matches("ownerSecret123", "hashedOwnerSecret")).thenReturn(true);
        when(jwt.generateToken(99L, "RESTAURANT_OWNER")).thenReturn("jwt-owner-99");

        AuthResponse resp = authService.login(req);

        assertThat(resp.token()).isEqualTo("jwt-owner-99");
        assertThat(resp.userId()).isEqualTo(99L);
        assertThat(resp.fullName()).isEqualTo("Chef Mario");
        assertThat(resp.role()).isEqualTo("RESTAURANT_OWNER");
    }

    @Test
    @DisplayName("login: returns matching role in token when user is ADMIN")
    void login_asAdmin_returnsAdminRole() {
        LoginRequest req = new LoginRequest("admin@quickbite.com", "adminKey123");
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@quickbite.com");
        admin.setPassword("hashedAdminKey");
        admin.setFullName("Platform Admin");
        admin.setRole(Role.ADMIN);

        when(repo.findByEmail("admin@quickbite.com")).thenReturn(Optional.of(admin));
        when(encoder.matches("adminKey123", "hashedAdminKey")).thenReturn(true);
        when(jwt.generateToken(1L, "ADMIN")).thenReturn("jwt-admin-1");

        AuthResponse resp = authService.login(req);

        assertThat(resp.token()).isEqualTo("jwt-admin-1");
        assertThat(resp.userId()).isEqualTo(1L);
        assertThat(resp.fullName()).isEqualTo("Platform Admin");
        assertThat(resp.role()).isEqualTo("ADMIN");
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
