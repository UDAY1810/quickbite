package com.quickbite.order_service.service;

import com.quickbite.order_service.client.MenuClient;
import com.quickbite.order_service.client.MenuItemDto;
import com.quickbite.order_service.dto.OrderItemRequest;
import com.quickbite.order_service.dto.OrderResponse;
import com.quickbite.order_service.dto.PlaceOrderRequest;
import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.entity.OrderStatus;
import com.quickbite.order_service.events.OrderPlacedEvent;
import com.quickbite.order_service.exception.BadRequestException;
import com.quickbite.order_service.exception.ForbiddenException;
import com.quickbite.order_service.exception.NotFoundException;
import com.quickbite.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuClient menuClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuClient, kafkaTemplate);
    }

    @Test
    @DisplayName("placeOrder: successfully calculates total, saves order and publishes OrderPlacedEvent")
    void placeOrder_success() {
        OrderItemRequest itemReq1 = new OrderItemRequest(101L, 2);
        OrderItemRequest itemReq2 = new OrderItemRequest(102L, 1);
        PlaceOrderRequest req = new PlaceOrderRequest(1L, "123 Baker St", List.of(itemReq1, itemReq2));

        MenuItemDto menu1 = new MenuItemDto(101L, "Burger", BigDecimal.valueOf(150.00), true);
        MenuItemDto menu2 = new MenuItemDto(102L, "Fries", BigDecimal.valueOf(80.00), true);
        when(menuClient.getItems(List.of(101L, 102L))).thenReturn(List.of(menu1, menu2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(500L);
            return o;
        });

        OrderResponse resp = orderService.placeOrder(req, 42L);

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(500L);
        assertThat(resp.customerId()).isEqualTo(42L);
        assertThat(resp.restaurantId()).isEqualTo(1L);
        // Total = 150 * 2 + 80 * 1 = 380.00
        assertThat(resp.totalAmount()).isEqualByComparingTo("380.00");
        assertThat(resp.status()).isEqualTo("PLACED");

        // Verify Kafka event published
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("order-events"), eq("500"), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(OrderPlacedEvent.class);
        OrderPlacedEvent event = (OrderPlacedEvent) eventCaptor.getValue();
        assertThat(event.orderId()).isEqualTo(500L);
        assertThat(event.customerId()).isEqualTo(42L);
        assertThat(event.totalAmount()).isEqualByComparingTo("380.00");
    }

    @Test
    @DisplayName("placeOrder: throws BadRequestException when menu item is unavailable")
    void placeOrder_unavailableItem_throwsBadRequest() {
        OrderItemRequest itemReq = new OrderItemRequest(101L, 1);
        PlaceOrderRequest req = new PlaceOrderRequest(1L, "123 Baker St", List.of(itemReq));

        MenuItemDto menu1 = new MenuItemDto(101L, "Burger", BigDecimal.valueOf(150.00), false);
        when(menuClient.getItems(List.of(101L))).thenReturn(List.of(menu1));

        assertThatThrownBy(() -> orderService.placeOrder(req, 42L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Item unavailable");

        verify(orderRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("myOrders: returns list of customer orders")
    void myOrders_returnsOrders() {
        Order o = new Order(42L, 1L, "Address");
        o.setId(10L);
        o.setTotalAmount(BigDecimal.valueOf(200.00));
        when(orderRepository.findByCustomerIdOrderByCreatedAtDesc(42L)).thenReturn(List.of(o));

        List<OrderResponse> orders = orderService.myOrders(42L);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).id()).isEqualTo(10L);
        verify(orderRepository).findByCustomerIdOrderByCreatedAtDesc(42L);
    }

    @Test
    @DisplayName("get: allows owner to view order")
    void get_asOwner_succeeds() {
        Order o = new Order(42L, 1L, "Address");
        o.setId(10L);
        o.setTotalAmount(BigDecimal.valueOf(200.00));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(o));

        OrderResponse resp = orderService.get(10L, 42L, "CUSTOMER");

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("get: allows ADMIN to view order of any customer")
    void get_asAdmin_succeeds() {
        Order o = new Order(42L, 1L, "Address");
        o.setId(10L);
        o.setTotalAmount(BigDecimal.valueOf(200.00));
        when(orderRepository.findById(10L)).thenReturn(Optional.of(o));

        OrderResponse resp = orderService.get(10L, 999L, "ADMIN");

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("get: throws ForbiddenException when customer tries to access another customer's order")
    void get_otherCustomer_throwsForbidden() {
        Order o = new Order(42L, 1L, "Address");
        o.setId(10L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(o));

        assertThatThrownBy(() -> orderService.get(10L, 99L, "CUSTOMER"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Not your order");
    }

    @Test
    @DisplayName("get: throws NotFoundException when order does not exist")
    void get_notFound_throwsNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.get(999L, 42L, "CUSTOMER"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Order not found");
    }
}
