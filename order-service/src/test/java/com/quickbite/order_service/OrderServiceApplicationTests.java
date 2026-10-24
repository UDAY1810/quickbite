package com.quickbite.order_service;

import com.quickbite.order_service.client.MenuClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderServiceApplicationTests {

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private MenuClient menuClient;

    @Test
    void contextLoads() {
    }
}
