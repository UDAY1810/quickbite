package com.quickbite.config_server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConfigServerApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("ConfigServer: Context loads and native profile is active")
    void contextLoads() {
        assertThat(environment.getProperty("spring.application.name")).isEqualTo("config-server");
        assertThat(environment.getActiveProfiles()).contains("native");
    }
}
