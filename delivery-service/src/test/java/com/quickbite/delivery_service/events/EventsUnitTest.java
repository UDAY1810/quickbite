package com.quickbite.delivery_service.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EventsUnitTest {

    @Test
    @DisplayName("DeliveryAssignedEvent: record accessors and equality")
    void deliveryAssignedEvent_accessors() {
        Instant now = Instant.now();
        DeliveryAssignedEvent event1 = new DeliveryAssignedEvent(500L, 20L, "Driver Dave", now);
        DeliveryAssignedEvent event2 = new DeliveryAssignedEvent(500L, 20L, "Driver Dave", now);

        assertThat(event1.orderId()).isEqualTo(500L);
        assertThat(event1.driverId()).isEqualTo(20L);
        assertThat(event1.driverName()).isEqualTo("Driver Dave");
        assertThat(event1.occurredAt()).isEqualTo(now);
        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    @DisplayName("OrderStatusChangedEvent: record accessors and equality")
    void orderStatusChangedEvent_accessors() {
        Instant now = Instant.now();
        OrderStatusChangedEvent event1 = new OrderStatusChangedEvent(100L, 50L, "CONFIRMED", now);
        OrderStatusChangedEvent event2 = new OrderStatusChangedEvent(100L, 50L, "CONFIRMED", now);

        assertThat(event1.orderId()).isEqualTo(100L);
        assertThat(event1.customerId()).isEqualTo(50L);
        assertThat(event1.status()).isEqualTo("CONFIRMED");
        assertThat(event1.occurredAt()).isEqualTo(now);
        assertThat(event1).isEqualTo(event2);
    }
}
