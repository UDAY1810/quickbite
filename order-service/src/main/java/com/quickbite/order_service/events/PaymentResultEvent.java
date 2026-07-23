package com.quickbite.order_service.events;



import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResultEvent(Long orderId, Long paymentId, BigDecimal amount,
                                 boolean success, String reason, Instant occurredAt) {}