package com.quickbite.order_service.kafka;

import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import com.quickbite.order_service.events.OrderStatusChangedEvent;
import com.quickbite.order_service.events.PaymentResultEvent;
import com.quickbite.order_service.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class PaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final OrderRepository orders;
    private final KafkaTemplate<String, Object> kafka;

    public PaymentEventListener(OrderRepository orders, KafkaTemplate<String, Object> kafka) {
        this.orders = orders;
        this.kafka = kafka;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-service")
    @Transactional
    public void onPaymentEvent(ConsumerRecord<String, PaymentResultEvent> record) {
        PaymentResultEvent e = record.value();
        if (e == null || e.orderId() == null) return;

        if (e.success()) {
            updateStatus(e.orderId(), OrderStatus.CONFIRMED);
        } else {
            log.warn("Payment failed for order {}: {}", e.orderId(), e.reason());
            updateStatus(e.orderId(), OrderStatus.CANCELLED);   // saga compensation
        }
    }

    private void updateStatus(Long orderId, OrderStatus status) {
        Order order = orders.findById(orderId).orElseThrow();
        order.setStatus(status);
        kafka.send("order-events", orderId.toString(),
                new OrderStatusChangedEvent(orderId, order.getCustomerId(),
                        status.name(), Instant.now()));
    }
}