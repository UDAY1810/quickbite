package com.quickbite.order_service.client;

import java.math.BigDecimal;

public record MenuItemDto(Long id, String name, BigDecimal price, boolean available) {}