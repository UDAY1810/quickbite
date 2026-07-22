package com.quickbite.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import java.util.List;

public record PlaceOrderRequest(
        @NotNull Long restaurantId,
        @NotBlank String deliveryAddress,
        @NotEmpty List<@Valid OrderItemRequest> items) {}