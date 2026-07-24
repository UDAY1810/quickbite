package com.quickbite.order_service.kafka;

import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import com.quickbite.order_service.events.OrderStatusChangedEvent;
import com.quickbite.order_service.repository.OrderRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeliveryStatusListener {

    private final OrderRepository orders;

    public DeliveryStatusListener(OrderRepository orders) {
        this.orders = orders;
    }

    @KafkaListener(topics = "order-events", groupId = "order-service-status")
    @Transactional
    public void onStatusEvent(ConsumerRecord<String, OrderStatusChangedEvent> record) {
        OrderStatusChangedEvent e = record.value();
        if (e == null || !"DELIVERED".equals(e.status())) return;
        orders.findById(e.orderId()).ifPresent(o -> o.setStatus(OrderStatus.DELIVERED));
    }
}