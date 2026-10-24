package com.quickbite.restaurant_service.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantEntityUnitTest {

    @Test
    @DisplayName("Restaurant: default values are open=true, rating=0, and menuItems initialized")
    void restaurant_defaults() {
        Restaurant r = new Restaurant();

        assertThat(r.isOpen()).isTrue();
        assertThat(r.getRating()).isEqualTo(BigDecimal.ZERO);
        assertThat(r.getMenuItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Restaurant: getters and setters work properly")
    void restaurant_gettersAndSetters() {
        Restaurant r = new Restaurant();
        r.setId(10L);
        r.setOwnerId(200L);
        r.setName("Pizza Paradise");
        r.setDescription("Wood-fired artisan pizzas");
        r.setAddress("123 Main St, New York");
        r.setCuisine("Italian");
        r.setImageUrl("https://example.com/pizza.jpg");
        r.setOpen(false);
        r.setRating(BigDecimal.valueOf(4.8));
        r.setMenuItems(new ArrayList<>());

        assertThat(r.getId()).isEqualTo(10L);
        assertThat(r.getOwnerId()).isEqualTo(200L);
        assertThat(r.getName()).isEqualTo("Pizza Paradise");
        assertThat(r.getDescription()).isEqualTo("Wood-fired artisan pizzas");
        assertThat(r.getAddress()).isEqualTo("123 Main St, New York");
        assertThat(r.getCuisine()).isEqualTo("Italian");
        assertThat(r.getImageUrl()).isEqualTo("https://example.com/pizza.jpg");
        assertThat(r.isOpen()).isFalse();
        assertThat(r.getRating()).isEqualTo(BigDecimal.valueOf(4.8));
        assertThat(r.getMenuItems()).isNotNull();
    }

    @Test
    @DisplayName("MenuItem: default available=true and getters/setters verify correctly")
    void menuItem_defaultsAndAccessors() {
        MenuItem item = new MenuItem();
        assertThat(item.isAvailable()).isTrue();

        Restaurant r = new Restaurant();
        r.setId(10L);

        item.setId(5L);
        item.setRestaurant(r);
        item.setName("Margherita");
        item.setDescription("San Marzano tomatoes, fresh mozzarella");
        item.setPrice(BigDecimal.valueOf(14.99));
        item.setImageUrl("https://example.com/margherita.jpg");
        item.setCategory("Pizza");
        item.setAvailable(false);

        assertThat(item.getId()).isEqualTo(5L);
        assertThat(item.getRestaurant()).isEqualTo(r);
        assertThat(item.getRestaurant().getId()).isEqualTo(10L);
        assertThat(item.getName()).isEqualTo("Margherita");
        assertThat(item.getDescription()).isEqualTo("San Marzano tomatoes, fresh mozzarella");
        assertThat(item.getPrice()).isEqualTo(BigDecimal.valueOf(14.99));
        assertThat(item.getImageUrl()).isEqualTo("https://example.com/margherita.jpg");
        assertThat(item.getCategory()).isEqualTo("Pizza");
        assertThat(item.isAvailable()).isFalse();
    }
}
