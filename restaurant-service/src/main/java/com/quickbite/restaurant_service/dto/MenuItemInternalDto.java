package com.quickbite.restaurant_service.dto;

import java.math.BigDecimal;

public record MenuItemInternalDto(Long id, String name, BigDecimal price, boolean available) {}