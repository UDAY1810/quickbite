package com.quickbite.delivery_service.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "driver_id", nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public Delivery() {}

    public Delivery(Long orderId, Long driverId, String status) {
        this.orderId = orderId;
        this.driverId = driverId;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getDriverId() { return driverId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}