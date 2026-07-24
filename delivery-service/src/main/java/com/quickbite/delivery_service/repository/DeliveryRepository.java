package com.quickbite.delivery_service.repository;

import com.quickbite.delivery_service.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsByOrderId(Long orderId);
}