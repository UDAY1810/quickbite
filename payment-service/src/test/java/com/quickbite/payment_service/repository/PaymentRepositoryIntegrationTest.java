package com.quickbite.payment_service.repository;

import com.quickbite.payment_service.entity.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PaymentRepositoryIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("PaymentRepository: save, existsByOrderId and findByOrderId")
    void testPaymentRepositoryOperations() {
        Payment payment = new Payment(100L, 5L, BigDecimal.valueOf(350.00), Payment.PENDING);
        payment.setRazorpayOrderId("order_rzp_mock123");

        paymentRepository.save(payment);

        assertThat(paymentRepository.existsByOrderId(100L)).isTrue();
        assertThat(paymentRepository.existsByOrderId(999L)).isFalse();

        Optional<Payment> found = paymentRepository.findByOrderId(100L);
        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualByComparingTo("350.00");
        assertThat(found.get().getStatus()).isEqualTo(Payment.PENDING);
        assertThat(found.get().getRazorpayOrderId()).isEqualTo("order_rzp_mock123");
    }
}
