package com.quickbite.delivery_service.kafka;

import com.quickbite.delivery_service.entity.Delivery;
import com.quickbite.delivery_service.entity.Driver;
import com.quickbite.delivery_service.events.OrderStatusChangedEvent;
import com.quickbite.delivery_service.repository.DeliveryRepository;
import com.quickbite.delivery_service.repository.DriverRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class DeliveryOrchestratorIntegrationTest {

    @Autowired
    private DeliveryOrchestrator orchestrator;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Driver assigned in DB and marked unavailable when CONFIRMED event arrives")
    void onOrderEvent_assignsDriverAndPersistsDelivery() {
        Driver driver = new Driver();
        driver.setName("Marco Rossi");
        driver.setAvailable(true);
        driver = driverRepository.save(driver);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(501L, 99L, "CONFIRMED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "501", event);

        orchestrator.onOrderEvent(record);

        // Verify delivery is stored in DB
        assertThat(deliveryRepository.existsByOrderId(501L)).isTrue();
        java.util.List<Delivery> deliveries = deliveryRepository.findAll();
        assertThat(deliveries).hasSize(1);
        Delivery delivery = deliveries.get(0);
        assertThat(delivery.getStatus()).isEqualTo("ASSIGNED");
        assertThat(delivery.getDriverId()).isEqualTo(driver.getId());

        // Verify driver is now marked unavailable
        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assertThat(updatedDriver.isAvailable()).isFalse();

        // Verify duplicate event does nothing (idempotency)
        orchestrator.onOrderEvent(record);
        assertThat(deliveryRepository.count()).isEqualTo(1);
    }
}
