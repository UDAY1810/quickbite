package com.quickbite.delivery_service.events;

import java.time.Instant;

public record DeliveryAssignedEvent(Long orderId, Long driverId,
                                    String driverName, Instant occurredAt) {}