package com.quickbite.restaurant_service.controller;

import com.quickbite.restaurant_service.dto.*;
import com.quickbite.restaurant_service.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<RestaurantResponse> list(@RequestParam(required = false) String search) {
        return service.list(search);
    }

    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest req,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String role) {
        return service.create(req, userId, role);
    }

    @PutMapping("/{id}")
    public RestaurantResponse update(@PathVariable Long id,
                                     @Valid @RequestBody RestaurantRequest req,
                                     @RequestHeader("X-User-Id") Long userId,
                                     @RequestHeader("X-User-Role") String role) {
        return service.update(id, req, userId, role);
    }

    @PostMapping("/{id}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse addMenuItem(@PathVariable Long id,
                                        @Valid @RequestBody MenuItemRequest req,
                                        @RequestHeader("X-User-Id") Long userId,
                                        @RequestHeader("X-User-Role") String role) {
        return service.addMenuItem(id, req, userId, role);
    }

    @PutMapping("/{id}/menu/{itemId}")
    public MenuItemResponse updateMenuItem(@PathVariable Long id,
                                           @PathVariable Long itemId,
                                           @Valid @RequestBody MenuItemRequest req,
                                           @RequestHeader("X-User-Id") Long userId,
                                           @RequestHeader("X-User-Role") String role) {
        return service.updateMenuItem(id, itemId, req, userId, role);
    }

    @GetMapping("/internal/menu-items")
    public List<MenuItemInternalDto> internalItems(@RequestParam List<Long> ids) {
        return service.internalItems(ids);
    }
}