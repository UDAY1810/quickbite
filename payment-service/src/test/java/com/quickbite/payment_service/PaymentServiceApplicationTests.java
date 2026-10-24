package com.quickbite.payment_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PaymentServiceApplicationTests {

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void contextLoads() {
    }
}
