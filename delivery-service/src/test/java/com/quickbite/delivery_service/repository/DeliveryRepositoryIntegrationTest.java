package com.quickbite.delivery_service.repository;

import com.quickbite.delivery_service.entity.Delivery;
import com.quickbite.delivery_service.entity.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeliveryRepositoryIntegrationTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("DriverRepository: finds first available driver")
    void testDriverAvailability() {
        Driver busyDriver = new Driver();
        busyDriver.setName("Busy Driver");
        busyDriver.setAvailable(false);
        driverRepository.save(busyDriver);

        Driver availableDriver = new Driver();
        availableDriver.setName("Available Driver");
        availableDriver.setAvailable(true);
        driverRepository.save(availableDriver);

        Optional<Driver> driverOpt = driverRepository.findFirstByAvailableTrue();
        assertThat(driverOpt).isPresent();
        assertThat(driverOpt.get().getName()).isEqualTo("Available Driver");
        assertThat(driverOpt.get().isAvailable()).isTrue();
    }

    @Test
    @DisplayName("DeliveryRepository: existsByOrderId checks delivery presence")
    void testDeliveryExists() {
        Delivery delivery = new Delivery(500L, 1L, "ASSIGNED");
        deliveryRepository.save(delivery);

        assertThat(deliveryRepository.existsByOrderId(500L)).isTrue();
        assertThat(deliveryRepository.existsByOrderId(999L)).isFalse();
    }
}
