package com.quickbite.discovery_server;

import com.netflix.eureka.EurekaServerContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiscoveryServerApplicationTests {

    @Autowired(required = false)
    private EurekaServerContext eurekaServerContext;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("DiscoveryServer: Context loads and Eureka Server initializes")
    void contextLoads() {
        assertThat(eurekaServerContext).isNotNull();
        assertThat(environment.getProperty("spring.application.name")).isEqualTo("discovery-server");
        assertThat(environment.getProperty("eureka.client.register-with-eureka")).isEqualTo("false");
        assertThat(environment.getProperty("eureka.client.fetch-registry")).isEqualTo("false");
    }
}
