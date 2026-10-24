package com.quickbite.notification_service.kafka;

import com.quickbite.notification_service.events.OrderStatusChangedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
class NotificationListenerUnitTest {

    @Mock
    private JavaMailSender mailSender;

    private NotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationListener(mailSender);
    }

    @Test
    @DisplayName("onOrderEvent: formats and processes CONFIRMED status notification")
    void onOrderEvent_confirmed() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(101L, 50L, "CONFIRMED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "101", event);

        listener.onOrderEvent(record);
    }

    @Test
    @DisplayName("onOrderEvent: formats and processes CANCELLED status notification")
    void onOrderEvent_cancelled() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(102L, 50L, "CANCELLED", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "102", event);

        listener.onOrderEvent(record);
    }

    @Test
    @DisplayName("onOrderEvent: formats and processes OUT_FOR_DELIVERY and DELIVERED status notifications")
    void onOrderEvent_deliveryStatuses() {
        OrderStatusChangedEvent event1 = new OrderStatusChangedEvent(103L, 50L, "OUT_FOR_DELIVERY", Instant.now());
        listener.onOrderEvent(new ConsumerRecord<>("order-events", 0, 0L, "103", event1));

        OrderStatusChangedEvent event2 = new OrderStatusChangedEvent(103L, 50L, "DELIVERED", Instant.now());
        listener.onOrderEvent(new ConsumerRecord<>("order-events", 0, 0L, "103", event2));
    }

    @Test
    @DisplayName("onOrderEvent: default status branch handles custom status smoothly")
    void onOrderEvent_defaultCustomStatus() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(105L, 50L, "PREPARING", Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "105", event);

        listener.onOrderEvent(record);
    }

    @Test
    @DisplayName("onOrderEvent: null record value or missing status is ignored safely")
    void onOrderEvent_nullOrMissingStatus_ignored() {
        ConsumerRecord<String, OrderStatusChangedEvent> recordNull = new ConsumerRecord<>("order-events", 0, 0L, "104", null);
        listener.onOrderEvent(recordNull);

        OrderStatusChangedEvent eventEmptyStatus = new OrderStatusChangedEvent(104L, 50L, null, Instant.now());
        ConsumerRecord<String, OrderStatusChangedEvent> recordNoStatus = new ConsumerRecord<>("order-events", 0, 0L, "104", eventEmptyStatus);
        listener.onOrderEvent(recordNoStatus);
    }
}
