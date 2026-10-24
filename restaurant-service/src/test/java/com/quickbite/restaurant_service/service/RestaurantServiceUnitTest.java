package com.quickbite.restaurant_service.service;

import com.quickbite.restaurant_service.dto.*;
import com.quickbite.restaurant_service.entity.MenuItem;
import com.quickbite.restaurant_service.entity.Restaurant;
import com.quickbite.restaurant_service.exception.ForbiddenException;
import com.quickbite.restaurant_service.exception.NotFoundException;
import com.quickbite.restaurant_service.repository.MenuItemRepository;
import com.quickbite.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceUnitTest {

    @Mock
    private RestaurantRepository restaurantRepo;

    @Mock
    private MenuItemRepository menuItemRepo;

    private RestaurantService service;

    @BeforeEach
    void setUp() {
        service = new RestaurantService(restaurantRepo, menuItemRepo);
    }

    @Test
    @DisplayName("list: calls findAll when search query is null or blank")
    void list_withoutSearch_returnsAll() {
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Pizza Palace");
        when(restaurantRepo.findAll()).thenReturn(List.of(r));

        List<RestaurantResponse> result = service.list(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Pizza Palace");
        verify(restaurantRepo).findAll();
        verify(restaurantRepo, never()).search(any());
    }

    @Test
    @DisplayName("list: calls search when search query is provided")
    void list_withSearch_callsSearch() {
        Restaurant r = new Restaurant();
        r.setId(2L);
        r.setName("Burger Hub");
        when(restaurantRepo.search("burger")).thenReturn(List.of(r));

        List<RestaurantResponse> result = service.list("burger");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Burger Hub");
        verify(restaurantRepo).search("burger");
    }

    @Test
    @DisplayName("get: returns restaurant with menu items")
    void get_existingRestaurant_returnsWithMenu() {
        Restaurant r = new Restaurant();
        r.setId(10L);
        r.setName("Taco Time");
        r.setMenuItems(new ArrayList<>());

        MenuItem item = new MenuItem();
        item.setId(101L);
        item.setName("Spicy Taco");
        item.setPrice(BigDecimal.valueOf(99.00));
        r.getMenuItems().add(item);

        when(restaurantRepo.findById(10L)).thenReturn(Optional.of(r));

        RestaurantResponse resp = service.get(10L);

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(10L);
        assertThat(resp.menu()).hasSize(1);
        assertThat(resp.menu().get(0).name()).isEqualTo("Spicy Taco");
    }

    @Test
    @DisplayName("get: throws NotFoundException when restaurant does not exist")
    void get_nonExisting_throwsNotFound() {
        when(restaurantRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    @DisplayName("create: succeeds when user is RESTAURANT_OWNER")
    void create_asOwner_succeeds() {
        RestaurantRequest req = new RestaurantRequest("Curry House", "Best curries", "Downtown", "Indian", "curry.jpg");
        Restaurant saved = new Restaurant();
        saved.setId(15L);
        saved.setOwnerId(100L);
        saved.setName(req.name());

        when(restaurantRepo.save(any(Restaurant.class))).thenReturn(saved);

        RestaurantResponse resp = service.create(req, 100L, "RESTAURANT_OWNER");

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(15L);
        assertThat(resp.name()).isEqualTo("Curry House");
        verify(restaurantRepo).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("create: throws ForbiddenException when role is CUSTOMER")
    void create_asCustomer_throwsForbidden() {
        RestaurantRequest req = new RestaurantRequest("Curry House", "Desc", "Loc", "Indian", null);

        assertThatThrownBy(() -> service.create(req, 100L, "CUSTOMER"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Only restaurant owners can do this");

        verify(restaurantRepo, never()).save(any());
    }

    @Test
    @DisplayName("update: throws ForbiddenException if user is not owner and not ADMIN")
    void update_nonOwner_throwsForbidden() {
        Restaurant r = new Restaurant();
        r.setId(5L);
        r.setOwnerId(100L);
        when(restaurantRepo.findById(5L)).thenReturn(Optional.of(r));

        RestaurantRequest req = new RestaurantRequest("Updated Name", "Desc", "Loc", "Cuisine", null);

        assertThatThrownBy(() -> service.update(5L, req, 200L, "RESTAURANT_OWNER"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Not your restaurant");
    }

    @Test
    @DisplayName("addMenuItem: adds menu item when requested by owner")
    void addMenuItem_asOwner_succeeds() {
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setOwnerId(50L);
        when(restaurantRepo.findById(1L)).thenReturn(Optional.of(r));

        MenuItemRequest itemReq = new MenuItemRequest("Garlic Bread", "Crispy", BigDecimal.valueOf(149.00), "Sides", "bread.jpg");
        MenuItem savedItem = new MenuItem();
        savedItem.setId(201L);
        savedItem.setName(itemReq.name());
        savedItem.setPrice(itemReq.price());

        when(menuItemRepo.save(any(MenuItem.class))).thenReturn(savedItem);

        MenuItemResponse resp = service.addMenuItem(1L, itemReq, 50L, "RESTAURANT_OWNER");

        assertThat(resp).isNotNull();
        assertThat(resp.id()).isEqualTo(201L);
        assertThat(resp.name()).isEqualTo("Garlic Bread");
        verify(menuItemRepo).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("internalItems: resolves MenuItemInternalDto by ids")
    void internalItems_resolvesDtos() {
        MenuItem m1 = new MenuItem();
        m1.setId(1L);
        m1.setName("Burger");
        m1.setPrice(BigDecimal.valueOf(199.00));
        m1.setAvailable(true);

        when(menuItemRepo.findByIdIn(List.of(1L))).thenReturn(List.of(m1));

        List<MenuItemInternalDto> dtos = service.internalItems(List.of(1L));

        assertThat(dtos).hasSize(1);
        assertThat(dtos.get(0).name()).isEqualTo("Burger");
        assertThat(dtos.get(0).price()).isEqualByComparingTo("199.00");
    }
}
