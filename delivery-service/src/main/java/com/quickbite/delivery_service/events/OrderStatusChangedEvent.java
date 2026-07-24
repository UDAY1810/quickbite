package com.quickbite.delivery_service.events;

import java.time.Instant;

public record OrderStatusChangedEvent(Long orderId, Long customerId,
                                      String status, Instant occurredAt) {}