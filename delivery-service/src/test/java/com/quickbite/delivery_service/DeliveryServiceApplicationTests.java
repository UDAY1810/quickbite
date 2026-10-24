package com.quickbite.delivery_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class DeliveryServiceApplicationTests {

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private SimpMessagingTemplate ws;

    @Test
    void contextLoads() {
    }
}
