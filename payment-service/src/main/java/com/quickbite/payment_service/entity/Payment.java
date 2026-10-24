package com.quickbite.payment_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    public static final String PENDING = "PENDING";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Payment() {}

    public Payment(Long orderId, BigDecimal amount, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public Payment(Long orderId, Long customerId, BigDecimal amount, String status) {
        this(orderId, amount, status);
        this.customerId = customerId;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
        this.updatedAt = Instant.now();
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
        this.updatedAt = Instant.now();
    }
}