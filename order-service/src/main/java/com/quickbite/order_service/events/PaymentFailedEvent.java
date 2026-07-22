package com.quickbite.order_service.events;

import java.time.Instant;

public record PaymentFailedEvent(Long orderId, String reason, Instant occurredAt) {}