package com.quickbite.payment_service.kafka;

import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.OrderPlacedEvent;
import com.quickbite.payment_service.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Records a PENDING payment for every newly placed order.
 *
 * The payment is NOT completed here any more: the customer pays through
 * Razorpay Checkout, and {@code PaymentController#verify} publishes the
 * PaymentResultEvent once the Razorpay signature has been verified.
 */
@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final PaymentRepository payments;

    public OrderEventListener(PaymentRepository payments) {
        this.payments = payments;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    public void onOrderEvent(ConsumerRecord<String, OrderPlacedEvent> record) {
        OrderPlacedEvent e = record.value();
        // order-events also carries OrderStatusChangedEvents, which deserialize
        // here without a totalAmount -- those are not new orders, ignore them.
        if (e == null || e.orderId() == null || e.totalAmount() == null) return;

        // IDEMPOTENCY: Kafka is at-least-once, we may see the event twice
        if (payments.existsByOrderId(e.orderId())) {
            log.info("Payment already recorded for order {}, skipping", e.orderId());
            return;
        }

        payments.save(new Payment(e.orderId(), e.customerId(), e.totalAmount(), Payment.PENDING));
        log.info("Order {} awaiting Razorpay payment of {}", e.orderId(), e.totalAmount());
    }
}