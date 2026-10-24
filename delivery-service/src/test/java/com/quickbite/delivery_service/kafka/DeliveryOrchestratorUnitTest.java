package com.quickbite.delivery_service.kafka;

import com.quickbite.delivery_service.entity.Delivery;
import com.quickbite.delivery_service.entity.Driver;
import com.quickbite.delivery_service.events.DeliveryAssignedEvent;
import com.quickbite.delivery_service.events.OrderStatusChangedEvent;
import com.quickbite.delivery_service.repository.DeliveryRepository;
import com.quickbite.delivery_service.repository.DriverRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryOrchestratorUnitTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SimpMessagingTemplate ws;

    private DeliveryOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new DeliveryOrchestrator(driverRepository, deliveryRepository, kafkaTemplate, ws);
    }

    @Test
    @DisplayName("onOrderEvent: CONFIRMED event assigns available driver and publishes DeliveryAssignedEvent")
    void onOrderEvent_confirmed_assignsDriver() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(100L, 10L, "CONFIRMED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        when(deliveryRepository.existsByOrderId(100L)).thenReturn(false);

        Driver driver = mock(Driver.class);
        when(driver.getId()).thenReturn(5L);
        when(driver.getName()).thenReturn("Alex Rider");
        when(driverRepository.findFirstByAvailableTrue()).thenReturn(Optional.of(driver));

        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orchestrator.onOrderEvent(record);

        verify(driver).setAvailable(false);
        verify(driverRepository).save(driver);

        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        Delivery savedDelivery = deliveryCaptor.getValue();
        assertThat(savedDelivery.getOrderId()).isEqualTo(100L);
        assertThat(savedDelivery.getDriverId()).isEqualTo(5L);
        assertThat(savedDelivery.getStatus()).isEqualTo("ASSIGNED");

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("delivery-events"), eq("100"), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(DeliveryAssignedEvent.class);
        DeliveryAssignedEvent assignedEvent = (DeliveryAssignedEvent) eventCaptor.getValue();
        assertThat(assignedEvent.orderId()).isEqualTo(100L);
        assertThat(assignedEvent.driverName()).isEqualTo("Alex Rider");
    }

    @Test
    @DisplayName("onOrderEvent: ignores when delivery already exists (idempotency)")
    void onOrderEvent_alreadyExists_skips() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(100L, 10L, "CONFIRMED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        when(deliveryRepository.existsByOrderId(100L)).thenReturn(true);

        orchestrator.onOrderEvent(record);

        verify(driverRepository, never()).findFirstByAvailableTrue();
        verify(deliveryRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("onOrderEvent: handles case when no driver is available")
    void onOrderEvent_noDriverAvailable_logsWarning() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(100L, 10L, "CONFIRMED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        when(deliveryRepository.existsByOrderId(100L)).thenReturn(false);
        when(driverRepository.findFirstByAvailableTrue()).thenReturn(Optional.empty());

        orchestrator.onOrderEvent(record);

        verify(deliveryRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("onOrderEvent: ignores non-CONFIRMED events")
    void onOrderEvent_nonConfirmed_ignored() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(100L, 10L, "CANCELLED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        orchestrator.onOrderEvent(record);

        verify(deliveryRepository, never()).existsByOrderId(any());
        verify(driverRepository, never()).findFirstByAvailableTrue();
    }
}
