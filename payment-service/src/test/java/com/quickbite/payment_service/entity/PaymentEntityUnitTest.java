package com.quickbite.payment_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentEntityUnitTest {

    @Test
    @DisplayName("Payment: constructor initializes orderId, customerId, amount, status and createdAt")
    void constructor_initializesFields() {
        Payment payment = new Payment(100L, 25L, BigDecimal.valueOf(350.00), Payment.PENDING);

        assertThat(payment.getOrderId()).isEqualTo(100L);
        assertThat(payment.getCustomerId()).isEqualTo(25L);
        assertThat(payment.getAmount()).isEqualTo(BigDecimal.valueOf(350.00));
        assertThat(payment.getStatus()).isEqualTo(Payment.PENDING);
        assertThat(payment.getRazorpayOrderId()).isNull();
        assertThat(payment.getRazorpayPaymentId()).isNull();
    }

    @Test
    @DisplayName("Payment: setStatus updates status and updatedAt timestamp")
    void setStatus_updatesTimestamp() {
        Payment payment = new Payment(100L, BigDecimal.valueOf(150.00), Payment.PENDING);
        assertThat(payment.getStatus()).isEqualTo("PENDING");

        payment.setStatus(Payment.COMPLETED);
        assertThat(payment.getStatus()).isEqualTo("COMPLETED");

        payment.setRazorpayOrderId("order_rzp_123");
        assertThat(payment.getRazorpayOrderId()).isEqualTo("order_rzp_123");

        payment.setRazorpayPaymentId("pay_rzp_456");
        assertThat(payment.getRazorpayPaymentId()).isEqualTo("pay_rzp_456");
    }
}
