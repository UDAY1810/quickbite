package com.quickbite.restaurant_service.service;

import com.quickbite.restaurant_service.dto.*;
import com.quickbite.restaurant_service.entity.MenuItem;
import com.quickbite.restaurant_service.entity.Restaurant;
import com.quickbite.restaurant_service.exception.ForbiddenException;
import com.quickbite.restaurant_service.exception.NotFoundException;
import com.quickbite.restaurant_service.repository.MenuItemRepository;
import com.quickbite.restaurant_service.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;

    public RestaurantService(RestaurantRepository r, MenuItemRepository m) {
        this.restaurants = r;
        this.menuItems = m;
    }

    public List<RestaurantResponse> list(String search) {
        List<Restaurant> found = (search == null || search.isBlank())
                ? restaurants.findAll()
                : restaurants.search(search);
        return found.stream().map(this::toResponseWithoutMenu).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse get(Long id) {
        Restaurant r = restaurants.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
        return toResponseWithMenu(r);
    }

    @Transactional
    public RestaurantResponse create(RestaurantRequest req, Long ownerId, String role) {
        requireOwner(role);
        Restaurant r = new Restaurant();
        r.setOwnerId(ownerId);
        apply(r, req);
        return toResponseWithoutMenu(restaurants.save(r));
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest req, Long userId, String role) {
        Restaurant r = ownedRestaurant(id, userId, role);
        apply(r, req);
        return toResponseWithoutMenu(r);
    }

    @Transactional
    public MenuItemResponse addMenuItem(Long restaurantId, MenuItemRequest req,
                                        Long userId, String role) {
        Restaurant r = ownedRestaurant(restaurantId, userId, role);
        MenuItem item = new MenuItem();
        item.setRestaurant(r);
        applyItem(item, req);
        return toItemResponse(menuItems.save(item));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(Long restaurantId, Long itemId,
                                           MenuItemRequest req, Long userId, String role) {
        ownedRestaurant(restaurantId, userId, role);
        MenuItem item = menuItems.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Menu item not found"));
        applyItem(item, req);
        return toItemResponse(item);
    }

    public List<MenuItemInternalDto> internalItems(List<Long> ids) {
        return menuItems.findByIdIn(ids).stream()
                .map(m -> new MenuItemInternalDto(m.getId(), m.getName(),
                        m.getPrice(), m.isAvailable()))
                .toList();
    }

    // ---- helpers ----
    private void requireOwner(String role) {
        if (!"RESTAURANT_OWNER".equals(role) && !"ADMIN".equals(role))
            throw new ForbiddenException("Only restaurant owners can do this");
    }

    private Restaurant ownedRestaurant(Long id, Long userId, String role) {
        Restaurant r = restaurants.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
        if (!"ADMIN".equals(role) && !r.getOwnerId().equals(userId))
            throw new ForbiddenException("Not your restaurant");
        return r;
    }

    private void apply(Restaurant r, RestaurantRequest req) {
        r.setName(req.name());
        r.setDescription(req.description());
        r.setAddress(req.address());
        r.setCuisine(req.cuisine());
        r.setImageUrl(req.imageUrl());
    }

    private void applyItem(MenuItem item, MenuItemRequest req) {
        item.setName(req.name());
        item.setDescription(req.description());
        item.setPrice(req.price());
        item.setCategory(req.category());
        item.setImageUrl(req.imageUrl());
    }

    private RestaurantResponse toResponseWithoutMenu(Restaurant r) {
        return new RestaurantResponse(r.getId(), r.getName(), r.getDescription(),
                r.getAddress(), r.getCuisine(), r.getRating(), r.isOpen(),
                r.getImageUrl(), List.of());
    }

    private RestaurantResponse toResponseWithMenu(Restaurant r) {
        List<MenuItemResponse> menu = r.getMenuItems().stream()
                .map(this::toItemResponse).toList();
        return new RestaurantResponse(r.getId(), r.getName(), r.getDescription(),
                r.getAddress(), r.getCuisine(), r.getRating(), r.isOpen(),
                r.getImageUrl(), menu);
    }

    private MenuItemResponse toItemResponse(MenuItem m) {
        return new MenuItemResponse(m.getId(), m.getName(), m.getDescription(),
                m.getPrice(), m.getCategory(), m.isAvailable(), m.getImageUrl());
    }
}