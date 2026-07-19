package com.quickbite.restaurant_service.dto;

import jakarta.validation.constraints.NotBlank;

public record RestaurantRequest(
        @NotBlank String name,
        String description,
        String address,
        String cuisine,
        String imageUrl) {}