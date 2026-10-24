package com.quickbite.payment_service.repository;

import com.quickbite.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
    Optional<Payment> findByOrderId(Long orderId);
}