package com.quickbite.order_service.kafka;

import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import com.quickbite.order_service.events.OrderStatusChangedEvent;
import com.quickbite.order_service.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryStatusListenerUnitTest {

    @Mock
    private OrderRepository orderRepository;

    private DeliveryStatusListener listener;

    @BeforeEach
    void setUp() {
        listener = new DeliveryStatusListener(orderRepository);
    }

    @Test
    @DisplayName("onStatusEvent: updates order to DELIVERED when status is DELIVERED")
    void onStatusEvent_delivered_updatesStatus() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(10L, 100L, "DELIVERED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "10", event);

        Order order = new Order(100L, 1L, "Address");
        order.setId(10L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        listener.onStatusEvent(record);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderRepository).findById(10L);
    }

    @Test
    @DisplayName("onStatusEvent: ignores non-DELIVERED statuses")
    void onStatusEvent_otherStatus_ignored() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(10L, 100L, "OUT_FOR_DELIVERY", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "10", event);

        listener.onStatusEvent(record);

        verify(orderRepository, never()).findById(any());
    }
}
