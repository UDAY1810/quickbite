package com.quickbite.order_service.repository;

import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("OrderRepository: save order with items and query by customerId")
    void saveAndFindOrdersByCustomerId() {
        Order order = new Order(10L, 1L, "456 Elm Street");
        order.addItem(100L, "Cheese Pizza", BigDecimal.valueOf(299.00), 2);
        order.setTotalAmount(BigDecimal.valueOf(598.00));
        order.setStatus(OrderStatus.PLACED);

        Order saved = orderRepository.save(order);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(1);
        assertThat(saved.getItems().get(0).getItemName()).isEqualTo("Cheese Pizza");

        List<Order> customerOrders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(10L);
        assertThat(customerOrders).hasSize(1);
        assertThat(customerOrders.get(0).getTotalAmount()).isEqualByComparingTo("598.00");
    }
}
