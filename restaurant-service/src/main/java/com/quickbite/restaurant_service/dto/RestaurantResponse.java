package com.quickbite.restaurant_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record RestaurantResponse(Long id, String name, String description,
                                 String address, String cuisine, BigDecimal rating, boolean open,
                                 String imageUrl, List<MenuItemResponse> menu) {}