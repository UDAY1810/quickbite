package com.quickbite.order_service.dto;

import com.quickbite.order_service.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoUnitTest {

    @Test
    @DisplayName("OrderItemRequest: accessors and constructor")
    void orderItemRequest_accessors() {
        OrderItemRequest req = new OrderItemRequest(10L, 3);
        assertThat(req.menuItemId()).isEqualTo(10L);
        assertThat(req.quantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("PlaceOrderRequest: accessors and constructor")
    void placeOrderRequest_accessors() {
        PlaceOrderRequest req = new PlaceOrderRequest(1L, "123 Main St", List.of(new OrderItemRequest(10L, 2)));
        assertThat(req.restaurantId()).isEqualTo(1L);
        assertThat(req.deliveryAddress()).isEqualTo("123 Main St");
        assertThat(req.items()).hasSize(1);
    }

    @Test
    @DisplayName("OrderResponse: accessors and nested items")
    void orderResponse_accessors() {
        OrderResponse.OrderItemResponse item = new OrderResponse.OrderItemResponse(
                20L, "Cheese Burger", BigDecimal.valueOf(150.0), 2
        );
        Instant now = Instant.now();
        OrderResponse res = new OrderResponse(
                100L, 50L, 1L, "PLACED", BigDecimal.valueOf(300.0), "456 Elm St", now, List.of(item)
        );

        assertThat(res.id()).isEqualTo(100L);
        assertThat(res.customerId()).isEqualTo(50L);
        assertThat(res.restaurantId()).isEqualTo(1L);
        assertThat(res.status()).isEqualTo("PLACED");
        assertThat(res.totalAmount()).isEqualTo(BigDecimal.valueOf(300.0));
        assertThat(res.deliveryAddress()).isEqualTo("456 Elm St");
        assertThat(res.createdAt()).isEqualTo(now);
        assertThat(res.items()).containsExactly(item);
    }

    @Test
    @DisplayName("OrderResponse: static from(Order) accurately maps entity to DTO")
    void orderResponse_fromEntity() {
        Order order = new Order(55L, 2L, "789 Park Ave");
        order.setId(200L);
        order.setTotalAmount(BigDecimal.valueOf(400.0));
        order.addItem(15L, "Garlic Bread", BigDecimal.valueOf(200.0), 2);

        OrderResponse res = OrderResponse.from(order);

        assertThat(res.id()).isEqualTo(200L);
        assertThat(res.customerId()).isEqualTo(55L);
        assertThat(res.restaurantId()).isEqualTo(2L);
        assertThat(res.status()).isEqualTo("PLACED");
        assertThat(res.totalAmount()).isEqualTo(BigDecimal.valueOf(400.0));
        assertThat(res.deliveryAddress()).isEqualTo("789 Park Ave");
        assertThat(res.items()).hasSize(1);
        assertThat(res.items().get(0).itemName()).isEqualTo("Garlic Bread");
        assertThat(res.items().get(0).quantity()).isEqualTo(2);
    }
}
