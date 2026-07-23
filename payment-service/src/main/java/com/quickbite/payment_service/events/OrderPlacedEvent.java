package com.quickbite.payment_service.events;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderPlacedEvent(Long orderId, Long customerId, Long restaurantId,
                               BigDecimal totalAmount, Instant occurredAt) {}