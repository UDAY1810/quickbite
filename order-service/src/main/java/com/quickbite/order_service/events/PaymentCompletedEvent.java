package com.quickbite.order_service.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentCompletedEvent(Long orderId, Long paymentId,
                                    BigDecimal amount, Instant occurredAt) {}