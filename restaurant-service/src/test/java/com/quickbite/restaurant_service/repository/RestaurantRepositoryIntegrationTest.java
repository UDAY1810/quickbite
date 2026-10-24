package com.quickbite.restaurant_service.repository;

import com.quickbite.restaurant_service.entity.MenuItem;
import com.quickbite.restaurant_service.entity.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RestaurantRepositoryIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    @DisplayName("RestaurantRepository: search by name and cuisine")
    void searchRestaurants() {
        Restaurant r1 = new Restaurant();
        r1.setOwnerId(1L);
        r1.setName("Pasta Fresca");
        r1.setCuisine("Italian");
        r1.setAddress("Rome St");
        restaurantRepository.save(r1);

        Restaurant r2 = new Restaurant();
        r2.setOwnerId(2L);
        r2.setName("Curry Pot");
        r2.setCuisine("Indian");
        r2.setAddress("Delhi Rd");
        restaurantRepository.save(r2);

        List<Restaurant> searchByName = restaurantRepository.search("pasta");
        assertThat(searchByName).hasSize(1);
        assertThat(searchByName.get(0).getName()).isEqualTo("Pasta Fresca");

        List<Restaurant> searchByCuisine = restaurantRepository.search("indian");
        assertThat(searchByCuisine).hasSize(1);
        assertThat(searchByCuisine.get(0).getName()).isEqualTo("Curry Pot");
    }

    @Test
    @DisplayName("MenuItemRepository: findByIdIn finds items by list of ids")
    void findMenuItemsByIdIn() {
        Restaurant r = new Restaurant();
        r.setOwnerId(1L);
        r.setName("Bistro");
        r = restaurantRepository.save(r);

        MenuItem item1 = new MenuItem();
        item1.setRestaurant(r);
        item1.setName("Item 1");
        item1.setPrice(BigDecimal.valueOf(50.0));
        item1 = menuItemRepository.save(item1);

        MenuItem item2 = new MenuItem();
        item2.setRestaurant(r);
        item2.setName("Item 2");
        item2.setPrice(BigDecimal.valueOf(100.0));
        item2 = menuItemRepository.save(item2);

        List<MenuItem> items = menuItemRepository.findByIdIn(List.of(item1.getId(), item2.getId()));
        assertThat(items).hasSize(2);
    }
}
