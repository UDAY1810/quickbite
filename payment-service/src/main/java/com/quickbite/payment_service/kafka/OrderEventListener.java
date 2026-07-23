package com.quickbite.payment_service.kafka;

import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.OrderPlacedEvent;
import com.quickbite.payment_service.events.PaymentCompletedEvent;
import com.quickbite.payment_service.events.PaymentFailedEvent;
import com.quickbite.payment_service.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final PaymentRepository payments;
    private final KafkaTemplate<String, Object> kafka;
    private final Random random = new Random();

    public OrderEventListener(PaymentRepository payments,
                              KafkaTemplate<String, Object> kafka) {
        this.payments = payments;
        this.kafka = kafka;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-service")
    public void onOrderEvent(ConsumerRecord<String, OrderPlacedEvent> record)
            throws InterruptedException {

        OrderPlacedEvent e = record.value();
        if (e == null || e.orderId() == null) return;

        // IDEMPOTENCY: Kafka is at-least-once, we may see the event twice
        if (payments.existsByOrderId(e.orderId())) {
            log.info("Payment already processed for order {}, skipping", e.orderId());
            return;
        }

        Thread.sleep(2000);                        // simulate gateway latency
        boolean success = random.nextInt(10) < 9;  // 90% success

        Payment p = new Payment(e.orderId(), e.totalAmount(),
                success ? "COMPLETED" : "FAILED");
        p = payments.save(p);

        Object result = success
                ? new PaymentCompletedEvent(e.orderId(), p.getId(),
                e.totalAmount(), Instant.now())
                : new PaymentFailedEvent(e.orderId(), "Card declined", Instant.now());

        kafka.send("payment-events", e.orderId().toString(), result);
        log.info("Order {} payment {}", e.orderId(), success ? "COMPLETED" : "FAILED");
    }
}