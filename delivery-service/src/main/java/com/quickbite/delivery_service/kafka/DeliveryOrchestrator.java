package com.quickbite.delivery_service.kafka;

import com.quickbite.delivery_service.entity.Delivery;
import com.quickbite.delivery_service.entity.Driver;
import com.quickbite.delivery_service.events.DeliveryAssignedEvent;
import com.quickbite.delivery_service.events.OrderStatusChangedEvent;
import com.quickbite.delivery_service.repository.DeliveryRepository;
import com.quickbite.delivery_service.repository.DriverRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DeliveryOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(DeliveryOrchestrator.class);

    private final DriverRepository drivers;
    private final DeliveryRepository deliveries;
    private final KafkaTemplate<String, Object> kafka;
    private final SimpMessagingTemplate ws;
    private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);

    public DeliveryOrchestrator(DriverRepository drivers, DeliveryRepository deliveries,
                                KafkaTemplate<String, Object> kafka,
                                SimpMessagingTemplate ws) {
        this.drivers = drivers;
        this.deliveries = deliveries;
        this.kafka = kafka;
        this.ws = ws;
    }

    @KafkaListener(topics = "order-events", groupId = "delivery-service")
    public void onOrderEvent(ConsumerRecord<String, OrderStatusChangedEvent> record) {
        OrderStatusChangedEvent e = record.value();
        if (e == null || !"CONFIRMED".equals(e.status())) return;
        if (deliveries.existsByOrderId(e.orderId())) return;   // idempotency

        Driver d = drivers.findFirstByAvailableTrue().orElse(null);
        if (d == null) {
            log.warn("No drivers available for order {}", e.orderId());
            return;
        }
        d.setAvailable(false);
        drivers.save(d);

        Delivery delivery = deliveries.save(
                new Delivery(e.orderId(), d.getId(), "ASSIGNED"));

        kafka.send("delivery-events", e.orderId().toString(),
                new DeliveryAssignedEvent(e.orderId(), d.getId(), d.getName(), Instant.now()));
        log.info("Driver {} assigned to order {}", d.getName(), e.orderId());

        simulateTrip(delivery, d);
    }

    private void simulateTrip(Delivery delivery, Driver driver) {
        final double[] lat = {12.9716};
        final double[] lng = {77.5946};

        for (int step = 1; step <= 10; step++) {
            final int s = step;
            pool.schedule(() -> {
                lat[0] += 0.0009;
                lng[0] += 0.0007;
                String status = s < 10 ? "OUT_FOR_DELIVERY" : "DELIVERED";

                ws.convertAndSend("/topic/orders/" + delivery.getOrderId(),
                        (Object) Map.of("orderId", delivery.getOrderId(),
                                "status", status,
                                "driver", driver.getName(),
                                "lat", lat[0],
                                "lng", lng[0],
                                "step", s));

                if (s == 10) {
                    kafka.send("order-events", delivery.getOrderId().toString(),
                            new OrderStatusChangedEvent(delivery.getOrderId(), null,
                                    "DELIVERED", Instant.now()));
                    driver.setAvailable(true);
                    drivers.save(driver);
                    log.info("Order {} delivered by {}", delivery.getOrderId(), driver.getName());
                }
            }, s * 3L, TimeUnit.SECONDS);
        }
    }
}