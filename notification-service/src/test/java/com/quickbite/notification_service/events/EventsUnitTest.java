package com.quickbite.notification_service.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EventsUnitTest {

    @Test
    @DisplayName("OrderStatusChangedEvent: accessors, equality, and hashcode")
    void orderStatusChangedEvent_accessors() {
        Instant now = Instant.now();
        OrderStatusChangedEvent event1 = new OrderStatusChangedEvent(101L, 42L, "CONFIRMED", now);
        OrderStatusChangedEvent event2 = new OrderStatusChangedEvent(101L, 42L, "CONFIRMED", now);

        assertThat(event1.orderId()).isEqualTo(101L);
        assertThat(event1.customerId()).isEqualTo(42L);
        assertThat(event1.status()).isEqualTo("CONFIRMED");
        assertThat(event1.occurredAt()).isEqualTo(now);
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }
}
