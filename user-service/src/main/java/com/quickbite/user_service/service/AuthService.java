package com.quickbite.user_service.service;

import com.quickbite.user_service.dto.AuthResponse;
import com.quickbite.user_service.dto.LoginRequest;
import com.quickbite.user_service.dto.RegisterRequest;
import com.quickbite.user_service.entity.User;
import com.quickbite.user_service.exception.ConflictException;
import com.quickbite.user_service.exception.UnauthorizedException;
import com.quickbite.user_service.repository.UserRepository;
import com.quickbite.user_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }
        User u = new User();
        u.setEmail(req.email());
        u.setPassword(encoder.encode(req.password()));
        u.setFullName(req.fullName());
        u.setPhone(req.phone());
        u = repo.save(u);
        return toResponse(u);
    }

    public AuthResponse login(LoginRequest req) {
        User u = repo.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Bad credentials"));
        if (!encoder.matches(req.password(), u.getPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }
        return toResponse(u);
    }

    private AuthResponse toResponse(User u) {
        String token = jwt.generateToken(u.getId(), u.getRole().name());
        return new AuthResponse(token, u.getId(), u.getFullName(), u.getRole().name());
    }
}