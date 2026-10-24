package com.quickbite.order_service.kafka;

import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import com.quickbite.order_service.events.OrderStatusChangedEvent;
import com.quickbite.order_service.events.PaymentResultEvent;
import com.quickbite.order_service.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private PaymentEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new PaymentEventListener(orderRepository, kafkaTemplate);
    }

    @Test
    @DisplayName("onPaymentEvent: success=true updates order to CONFIRMED and publishes OrderStatusChangedEvent")
    void onPaymentEvent_success_updatesToConfirmed() {
        PaymentResultEvent event = new PaymentResultEvent(10L, 501L, BigDecimal.valueOf(250.00), true, null, Instant.now());
        ConsumerRecord<String, PaymentResultEvent> record = new ConsumerRecord<>("payment-events", 0, 0L, "10", event);

        Order order = new Order(100L, 1L, "Address");
        order.setId(10L);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        listener.onPaymentEvent(record);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("order-events"), eq("10"), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(OrderStatusChangedEvent.class);
        OrderStatusChangedEvent statusEvent = (OrderStatusChangedEvent) captor.getValue();
        assertThat(statusEvent.orderId()).isEqualTo(10L);
        assertThat(statusEvent.status()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("onPaymentEvent: success=false triggers saga compensation -> CANCELLED")
    void onPaymentEvent_failed_triggersCancellation() {
        PaymentResultEvent event = new PaymentResultEvent(20L, 502L, BigDecimal.valueOf(150.00), false, "Card declined", Instant.now());
        ConsumerRecord<String, PaymentResultEvent> record = new ConsumerRecord<>("payment-events", 0, 0L, "20", event);

        Order order = new Order(200L, 2L, "Address");
        order.setId(20L);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(20L)).thenReturn(Optional.of(order));

        listener.onPaymentEvent(record);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("order-events"), eq("20"), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(OrderStatusChangedEvent.class);
        OrderStatusChangedEvent statusEvent = (OrderStatusChangedEvent) captor.getValue();
        assertThat(statusEvent.orderId()).isEqualTo(20L);
        assertThat(statusEvent.status()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("onPaymentEvent: null or invalid event ignored gracefully")
    void onPaymentEvent_nullEvent_ignored() {
        ConsumerRecord<String, PaymentResultEvent> record = new ConsumerRecord<>("payment-events", 0, 0L, "key", null);

        listener.onPaymentEvent(record);

        verify(orderRepository, never()).findById(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("onPaymentEvent: event with null orderId ignored")
    void onPaymentEvent_nullOrderId_ignored() {
        PaymentResultEvent event = new PaymentResultEvent(null, 503L, BigDecimal.valueOf(99.00), true, null, Instant.now());
        ConsumerRecord<String, PaymentResultEvent> record = new ConsumerRecord<>("payment-events", 0, 0L, "key", event);

        listener.onPaymentEvent(record);

        verify(orderRepository, never()).findById(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("onPaymentEvent: throws NoSuchElementException when order not found in database")
    void onPaymentEvent_orderNotFound_throwsException() {
        PaymentResultEvent event = new PaymentResultEvent(999L, 501L, BigDecimal.valueOf(100.00), true, null, Instant.now());
        ConsumerRecord<String, PaymentResultEvent> record = new ConsumerRecord<>("payment-events", 0, 0L, "999", event);

        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listener.onPaymentEvent(record))
                .isInstanceOf(NoSuchElementException.class);
    }
}
