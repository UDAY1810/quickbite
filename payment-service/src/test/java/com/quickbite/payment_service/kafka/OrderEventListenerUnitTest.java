package com.quickbite.payment_service.kafka;

import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.OrderPlacedEvent;
import com.quickbite.payment_service.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerUnitTest {

    @Mock
    private PaymentRepository paymentRepository;

    private OrderEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new OrderEventListener(paymentRepository);
    }

    @Test
    @DisplayName("onOrderEvent: saves PENDING payment for new order")
    void onOrderEvent_createsPendingPayment() {
        OrderPlacedEvent event = new OrderPlacedEvent(100L, 25L, 2L, BigDecimal.valueOf(600.00), Instant.now());
        ConsumerRecord<String, OrderPlacedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        when(paymentRepository.existsByOrderId(100L)).thenReturn(false);

        listener.onOrderEvent(record);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(100L);
        assertThat(saved.getCustomerId()).isEqualTo(25L);
        assertThat(saved.getAmount()).isEqualByComparingTo("600.00");
        assertThat(saved.getStatus()).isEqualTo(Payment.PENDING);
    }

    @Test
    @DisplayName("onOrderEvent: ignores duplicate order event (idempotency)")
    void onOrderEvent_duplicate_skipsSave() {
        OrderPlacedEvent event = new OrderPlacedEvent(100L, 25L, 2L, BigDecimal.valueOf(600.00), Instant.now());
        ConsumerRecord<String, OrderPlacedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        when(paymentRepository.existsByOrderId(100L)).thenReturn(true);

        listener.onOrderEvent(record);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("onOrderEvent: ignores events without totalAmount (like OrderStatusChangedEvent)")
    void onOrderEvent_missingAmount_ignored() {
        OrderPlacedEvent event = new OrderPlacedEvent(100L, 25L, 2L, null, Instant.now());
        ConsumerRecord<String, OrderPlacedEvent> record = new ConsumerRecord<>("order-events", 0, 0L, "100", event);

        listener.onOrderEvent(record);

        verify(paymentRepository, never()).existsByOrderId(any());
        verify(paymentRepository, never()).save(any());
    }
}
