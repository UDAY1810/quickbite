package com.quickbite.restaurant_service.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoUnitTest {

    @Test
    @DisplayName("RestaurantRequest: constructor and accessors")
    void restaurantRequest_accessors() {
        RestaurantRequest req = new RestaurantRequest(
                "Curry House", "Authentic Indian cuisine", "45 Park Street", "Indian", "curry.jpg"
        );

        assertThat(req.name()).isEqualTo("Curry House");
        assertThat(req.description()).isEqualTo("Authentic Indian cuisine");
        assertThat(req.address()).isEqualTo("45 Park Street");
        assertThat(req.cuisine()).isEqualTo("Indian");
        assertThat(req.imageUrl()).isEqualTo("curry.jpg");
    }

    @Test
    @DisplayName("MenuItemRequest: constructor and accessors")
    void menuItemRequest_accessors() {
        MenuItemRequest req = new MenuItemRequest(
                "Butter Chicken", "Rich creamy gravy", BigDecimal.valueOf(320.0), "Curry", "butter_chicken.jpg"
        );

        assertThat(req.name()).isEqualTo("Butter Chicken");
        assertThat(req.description()).isEqualTo("Rich creamy gravy");
        assertThat(req.price()).isEqualTo(BigDecimal.valueOf(320.0));
        assertThat(req.category()).isEqualTo("Curry");
        assertThat(req.imageUrl()).isEqualTo("butter_chicken.jpg");
    }

    @Test
    @DisplayName("RestaurantResponse: constructor and accessors with nested menu")
    void restaurantResponse_accessors() {
        MenuItemResponse item = new MenuItemResponse(
                1L, "Naan", "Fresh garlic naan", BigDecimal.valueOf(40.0), "Breads", true, "naan.jpg"
        );
        RestaurantResponse res = new RestaurantResponse(
                10L, "Curry House", "Authentic Indian cuisine", "45 Park Street", "Indian",
                BigDecimal.valueOf(4.6), true, "curry.jpg", List.of(item)
        );

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.name()).isEqualTo("Curry House");
        assertThat(res.rating()).isEqualTo(BigDecimal.valueOf(4.6));
        assertThat(res.open()).isTrue();
        assertThat(res.menu()).hasSize(1);
        assertThat(res.menu().get(0).name()).isEqualTo("Naan");
        assertThat(res.menu().get(0).available()).isTrue();
    }

    @Test
    @DisplayName("MenuItemInternalDto: constructor and accessors")
    void menuItemInternalDto_accessors() {
        MenuItemInternalDto dto = new MenuItemInternalDto(5L, "Lassi", BigDecimal.valueOf(60.0), true);

        assertThat(dto.id()).isEqualTo(5L);
        assertThat(dto.name()).isEqualTo("Lassi");
        assertThat(dto.price()).isEqualTo(BigDecimal.valueOf(60.0));
        assertThat(dto.available()).isTrue();
    }
}
