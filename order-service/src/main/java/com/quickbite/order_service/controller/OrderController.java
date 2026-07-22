package com.quickbite.order_service.controller;

import com.quickbite.order_service.dto.OrderResponse;
import com.quickbite.order_service.dto.PlaceOrderRequest;
import com.quickbite.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@Valid @RequestBody PlaceOrderRequest req,
                               @RequestHeader("X-User-Id") Long userId) {
        return service.placeOrder(req, userId);
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(@RequestHeader("X-User-Id") Long userId) {
        return service.myOrders(userId);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id,
                             @RequestHeader("X-User-Id") Long userId,
                             @RequestHeader("X-User-Role") String role) {
        return service.get(id, userId, role);
    }
}