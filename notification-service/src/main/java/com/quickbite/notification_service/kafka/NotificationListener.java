package com.quickbite.notification_service.kafka;

import com.quickbite.notification_service.events.OrderStatusChangedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final JavaMailSender mail;

    public NotificationListener(JavaMailSender mail) {
        this.mail = mail;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service")
    public void onOrderEvent(ConsumerRecord<String, OrderStatusChangedEvent> record) {
        OrderStatusChangedEvent e = record.value();
        if (e == null || e.status() == null) return;

        String subject = "QuickBite: order #" + e.orderId() + " is " + e.status();
        String body = switch (e.status()) {
            case "CONFIRMED"        -> "Payment received! The restaurant is preparing your food.";
            case "CANCELLED"        -> "Sorry - your payment failed and the order was cancelled.";
            case "OUT_FOR_DELIVERY" -> "Your food is on the way!";
            case "DELIVERED"        -> "Delivered. Enjoy your meal!";
            default                 -> "Your order status is now: " + e.status();
        };

        send(subject, body);
        log.info("Notification sent for order {} ({})", e.orderId(), e.status());
    }

    private void send(String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("noreply@quickbite.dev");
            msg.setTo("customer@quickbite.dev");
            msg.setSubject(subject);
            msg.setText(body);
            mail.send(msg);
        } catch (Exception ex) {
            log.error("Mail failed: {}", ex.getMessage());
        }
    }
}