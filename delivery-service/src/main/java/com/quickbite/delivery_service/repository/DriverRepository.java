package com.quickbite.delivery_service.repository;

import com.quickbite.delivery_service.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findFirstByAvailableTrue();
}