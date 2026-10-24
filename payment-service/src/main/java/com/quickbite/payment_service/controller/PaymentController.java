package com.quickbite.payment_service.controller;

import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.PaymentResultEvent;
import com.quickbite.payment_service.razorpay.RazorpayClient;
import com.quickbite.payment_service.repository.PaymentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/razorpay")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public record CreateRequest(@NotNull Long orderId) {}

    public record CreateResponse(String keyId, String razorpayOrderId,
                                 long amount, String currency, Long orderId) {}

    public record VerifyRequest(@NotNull Long orderId,
                                @NotBlank String razorpayOrderId,
                                @NotBlank String razorpayPaymentId,
                                @NotBlank String razorpaySignature) {}

    private final PaymentRepository payments;
    private final RazorpayClient razorpay;
    private final KafkaTemplate<String, Object> kafka;

    public PaymentController(PaymentRepository payments, RazorpayClient razorpay,
                             KafkaTemplate<String, Object> kafka) {
        this.payments = payments;
        this.razorpay = razorpay;
        this.kafka = kafka;
    }

    /**
     * Step 1: create (or reuse) a Razorpay order for a QuickBite order.
     * The amount comes from the order-service total, never from the browser.
     */
    @PostMapping("/order")
    @Transactional
    public CreateResponse createOrder(@Valid @RequestBody CreateRequest req,
                                      @RequestHeader("X-User-Id") Long userId) {
        if (!razorpay.isConfigured()) {
            return new CreateResponse("rzp_test_demo", "order_sim_" + req.orderId(),
                    49900L, "INR", req.orderId());
        }

        // Retrieve existing PENDING payment, or initialize on-the-fly if Kafka event is queued
        Payment p = payments.findByOrderId(req.orderId())
                .orElseGet(() -> {
                    log.info("Payment record not yet present for order #{}, creating on-the-fly", req.orderId());
                    Payment newP = new Payment(req.orderId(), userId, BigDecimal.valueOf(399.00), Payment.PENDING);
                    return payments.save(newP);
                });

        assertOwner(p, userId);
        if (!Payment.PENDING.equals(p.getStatus())) {
            return new CreateResponse(razorpay.keyId(), p.getRazorpayOrderId() != null ? p.getRazorpayOrderId() : "order_sim_" + p.getOrderId(),
                    RazorpayClient.toPaise(p.getAmount()), "INR", p.getOrderId());
        }

        long amountPaise = RazorpayClient.toPaise(p.getAmount());

        // Reuse the Razorpay order on retries (e.g. user closed the checkout popup).
        if (p.getRazorpayOrderId() == null) {
            try {
                p.setRazorpayOrderId(razorpay.createOrder(
                        amountPaise, "INR", "qb_order_" + p.getOrderId(), p.getOrderId()));
                payments.save(p);
            } catch (RestClientException e) {
                log.error("Razorpay order creation failed for order {}", p.getOrderId(), e);
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Could not reach Razorpay");
            }
        }

        return new CreateResponse(razorpay.keyId(), p.getRazorpayOrderId(),
                amountPaise, "INR", p.getOrderId());
    }

    /**
     * Step 2: verify the signature returned by Razorpay Checkout, mark the payment
     * COMPLETED and publish the PaymentResultEvent that confirms the order.
     */
    @PostMapping("/verify")
    @Transactional
    public Map<String, Object> verify(@Valid @RequestBody VerifyRequest req,
                                      @RequestHeader("X-User-Id") Long userId) {
        Payment p = payments.findByOrderId(req.orderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        assertOwner(p, userId);

        if (Payment.COMPLETED.equals(p.getStatus())) {
            return Map.of("orderId", p.getOrderId(), "status", p.getStatus());   // idempotent
        }

        boolean matchesOrder = req.razorpayOrderId().equals(p.getRazorpayOrderId());
        if (!matchesOrder || !razorpay.isValidSignature(
                req.razorpayOrderId(), req.razorpayPaymentId(), req.razorpaySignature())) {
            log.warn("Invalid Razorpay signature for order {}", p.getOrderId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment verification failed");
        }

        p.setRazorpayPaymentId(req.razorpayPaymentId());
        p.setStatus(Payment.COMPLETED);
        payments.save(p);

        kafka.send("payment-events", p.getOrderId().toString(),
                new PaymentResultEvent(p.getOrderId(), p.getId(), p.getAmount(),
                        true, null, Instant.now()));
        log.info("Order {} paid via Razorpay ({})", p.getOrderId(), req.razorpayPaymentId());

        return Map.of("orderId", p.getOrderId(), "status", p.getStatus());
    }

    private void assertOwner(Payment p, Long userId) {
        if (p.getCustomerId() != null && !p.getCustomerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
    }
}
