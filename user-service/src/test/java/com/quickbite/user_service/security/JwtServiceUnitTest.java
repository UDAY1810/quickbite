package com.quickbite.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceUnitTest {

    private static final String SECRET = "a-very-long-and-secure-secret-key-for-quickbite-test-purposes-123456";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    private JwtService jwtService;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("generateToken: creates valid token with userId and role")
    void generateToken_containsCorrectClaims() {
        String token = jwtService.generateToken(42L, "RESTAURANT_OWNER");

        assertThat(token).isNotBlank();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.get("role", String.class)).isEqualTo("RESTAURANT_OWNER");
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }
}
