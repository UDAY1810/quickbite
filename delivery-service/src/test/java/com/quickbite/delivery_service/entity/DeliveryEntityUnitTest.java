package com.quickbite.delivery_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryEntityUnitTest {

    @Test
    @DisplayName("Delivery: parameterized constructor and accessors work as expected")
    void delivery_constructorAndAccessors() {
        Delivery delivery = new Delivery(1001L, 42L, "ASSIGNED");

        assertThat(delivery.getOrderId()).isEqualTo(1001L);
        assertThat(delivery.getDriverId()).isEqualTo(42L);
        assertThat(delivery.getStatus()).isEqualTo("ASSIGNED");

        delivery.setStatus("DELIVERED");
        assertThat(delivery.getStatus()).isEqualTo("DELIVERED");
    }

    @Test
    @DisplayName("Delivery: no-arg constructor creates empty instance")
    void delivery_noArgConstructor() {
        Delivery delivery = new Delivery();
        assertThat(delivery.getId()).isNull();
        assertThat(delivery.getOrderId()).isNull();
        assertThat(delivery.getDriverId()).isNull();
        assertThat(delivery.getStatus()).isNull();
    }

    @Test
    @DisplayName("Delivery: status transitions cycle through stages")
    void delivery_statusTransitions() {
        Delivery delivery = new Delivery(2002L, 10L, "ASSIGNED");
        assertThat(delivery.getStatus()).isEqualTo("ASSIGNED");

        delivery.setStatus("OUT_FOR_DELIVERY");
        assertThat(delivery.getStatus()).isEqualTo("OUT_FOR_DELIVERY");

        delivery.setStatus("DELIVERED");
        assertThat(delivery.getStatus()).isEqualTo("DELIVERED");
    }

    @Test
    @DisplayName("Driver: default availability is true and setters update fields")
    void driver_defaultsAndSetters() {
        Driver driver = new Driver();
        assertThat(driver.isAvailable()).isTrue();

        driver.setName("John Walker");
        driver.setAvailable(false);

        assertThat(driver.getName()).isEqualTo("John Walker");
        assertThat(driver.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Driver: availability toggling between true and false")
    void driver_availabilityToggling() {
        Driver driver = new Driver();
        assertThat(driver.isAvailable()).isTrue();

        driver.setAvailable(false);
        assertThat(driver.isAvailable()).isFalse();

        driver.setAvailable(true);
        assertThat(driver.isAvailable()).isTrue();
    }
}
