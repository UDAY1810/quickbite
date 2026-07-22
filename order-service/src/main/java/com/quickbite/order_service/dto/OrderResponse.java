package com.quickbite.order_service.dto;

import com.quickbite.order_service.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long id, Long customerId, Long restaurantId,
                            String status, BigDecimal totalAmount, String deliveryAddress,
                            Instant createdAt, List<OrderItemResponse> items) {

    public record OrderItemResponse(Long menuItemId, String itemName,
                                    BigDecimal unitPrice, int quantity) {}

    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getCustomerId(), o.getRestaurantId(),
                o.getStatus().name(), o.getTotalAmount(), o.getDeliveryAddress(),
                o.getCreatedAt(),
                o.getItems().stream()
                        .map(i -> new OrderItemResponse(i.getMenuItemId(),
                                i.getItemName(), i.getUnitPrice(), i.getQuantity()))
                        .toList());
    }
}