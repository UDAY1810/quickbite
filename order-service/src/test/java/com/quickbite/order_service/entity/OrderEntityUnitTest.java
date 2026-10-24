package com.quickbite.order_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityUnitTest {

    @Test
    @DisplayName("Order: default status is PLACED, totalAmount is ZERO, and timestamps are initialized")
    void order_defaults() {
        Order order = new Order();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();
        assertThat(order.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Order: addItem correctly attaches OrderItem and associates back-reference")
    void order_addItem() {
        Order order = new Order(101L, 202L, "221B Baker St");
        order.addItem(55L, "Chicken Tikka", BigDecimal.valueOf(320.0), 2);

        assertThat(order.getItems()).hasSize(1);
        OrderItem item = order.getItems().get(0);
        assertThat(item.getOrder()).isSameAs(order);
        assertThat(item.getMenuItemId()).isEqualTo(55L);
        assertThat(item.getItemName()).isEqualTo("Chicken Tikka");
        assertThat(item.getUnitPrice()).isEqualTo(BigDecimal.valueOf(320.0));
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Order: setStatus updates both status and updatedAt timestamp")
    void order_statusUpdate_updatesTimestamp() throws InterruptedException {
        Order order = new Order();
        Instant initialUpdate = order.getUpdatedAt();

        Thread.sleep(10);
        order.setStatus(OrderStatus.CONFIRMED);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getUpdatedAt()).isAfterOrEqualTo(initialUpdate);
    }
}
