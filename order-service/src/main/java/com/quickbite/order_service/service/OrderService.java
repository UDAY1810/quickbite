package com.quickbite.order_service.service;

import com.quickbite.order_service.client.MenuClient;
import com.quickbite.order_service.client.MenuItemDto;
import com.quickbite.order_service.dto.*;
import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.events.OrderPlacedEvent;
import com.quickbite.order_service.exception.BadRequestException;
import com.quickbite.order_service.exception.ForbiddenException;
import com.quickbite.order_service.exception.NotFoundException;
import com.quickbite.order_service.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final MenuClient menuClient;
    private final KafkaTemplate<String, Object> kafka;

    public OrderService(OrderRepository orders, MenuClient menuClient,
                        KafkaTemplate<String, Object> kafka) {
        this.orders = orders;
        this.menuClient = menuClient;
        this.kafka = kafka;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest req, Long customerId) {
        List<Long> ids = req.items().stream()
                .map(OrderItemRequest::menuItemId).toList();

        Map<Long, MenuItemDto> menu = menuClient.getItems(ids).stream()
                .collect(Collectors.toMap(MenuItemDto::id, Function.identity()));

        Order order = new Order(customerId, req.restaurantId(), req.deliveryAddress());
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest it : req.items()) {
            MenuItemDto m = menu.get(it.menuItemId());
            if (m == null || !m.available()) {
                throw new BadRequestException("Item unavailable: " + it.menuItemId());
            }
            order.addItem(m.id(), m.name(), m.price(), it.quantity());
            total = total.add(m.price().multiply(BigDecimal.valueOf(it.quantity())));
        }
        order.setTotalAmount(total);
        order = orders.save(order);

        kafka.send("order-events", order.getId().toString(),
                new OrderPlacedEvent(order.getId(), customerId,
                        req.restaurantId(), total, Instant.now()));

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(Long customerId) {
        return orders.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id, Long userId, String role) {
        Order o = orders.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!"ADMIN".equals(role) && !o.getCustomerId().equals(userId)) {
            throw new ForbiddenException("Not your order");
        }
        return OrderResponse.from(o);
    }
}