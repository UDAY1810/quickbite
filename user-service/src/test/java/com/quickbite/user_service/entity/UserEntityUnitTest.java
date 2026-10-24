package com.quickbite.user_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityUnitTest {

    @Test
    @DisplayName("User: default role is CUSTOMER and createdAt is populated")
    void defaultValues_areInitialized() {
        User user = new User();

        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("User: getters and setters update fields correctly")
    void gettersAndSetters_workAsExpected() {
        User user = new User();
        user.setId(50L);
        user.setEmail("test@quickbite.com");
        user.setPassword("secretHash");
        user.setFullName("Sam Winchester");
        user.setPhone("9988776655");
        user.setRole(Role.ADMIN);
        Instant fixedTime = Instant.parse("2026-01-01T12:00:00Z");
        user.setCreatedAt(fixedTime);

        assertThat(user.getId()).isEqualTo(50L);
        assertThat(user.getEmail()).isEqualTo("test@quickbite.com");
        assertThat(user.getPassword()).isEqualTo("secretHash");
        assertThat(user.getFullName()).isEqualTo("Sam Winchester");
        assertThat(user.getPhone()).isEqualTo("9988776655");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getCreatedAt()).isEqualTo(fixedTime);
    }
}
