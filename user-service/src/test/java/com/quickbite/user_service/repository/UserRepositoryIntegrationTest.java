package com.quickbite.user_service.repository;

import com.quickbite.user_service.entity.Role;
import com.quickbite.user_service.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("UserRepository: save, findByEmail and existsByEmail")
    void testUserRepositoryOperations() {
        User user = new User();
        user.setEmail("repo-test@quickbite.com");
        user.setPassword("hashedpassword123");
        user.setFullName("Repo Test User");
        user.setPhone("1234567890");
        user.setRole(Role.CUSTOMER);

        userRepository.save(user);

        assertThat(userRepository.existsByEmail("repo-test@quickbite.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@quickbite.com")).isFalse();

        Optional<User> found = userRepository.findByEmail("repo-test@quickbite.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Repo Test User");
        assertThat(found.get().getRole()).isEqualTo(Role.CUSTOMER);
    }
}
