package com.quickbite.api_gateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterUnitTest {

    private static final String SECRET = "gateway-test-super-secret-key-32characters-long-quickbite";

    @Mock
    private GatewayFilterChain chain;

    private JwtAuthFilter filter;
    private SecretKey key;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthFilter(SECRET);
        key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("filter: allows open paths without Authorization header")
    void openPaths_allowUnauthenticated() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/login").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("filter: returns 401 UNAUTHORIZED when Authorization header is missing on protected path")
    void protectedPath_missingAuthHeader_returns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("filter: returns 401 UNAUTHORIZED when Authorization header does not start with Bearer ")
    void protectedPath_invalidAuthHeaderScheme_returns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @DisplayName("filter: mutates request with X-User-Id and X-User-Role on valid JWT")
    void protectedPath_validJwt_addsUserHeaders() {
        String token = Jwts.builder()
                .subject("123")
                .claim("role", "CUSTOMER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(key)
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(captor.capture());

        ServerWebExchange mutatedExchange = captor.getValue();
        assertThat(mutatedExchange.getRequest().getHeaders().getFirst("X-User-Id")).isEqualTo("123");
        assertThat(mutatedExchange.getRequest().getHeaders().getFirst("X-User-Role")).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("filter: returns 401 UNAUTHORIZED when token has invalid signature")
    void protectedPath_invalidTokenSignature_returns401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }
}
