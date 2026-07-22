package com.quickbite.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "restaurant-service")
public interface MenuClient {
    @GetMapping("/api/restaurants/internal/menu-items")
    List<MenuItemDto> getItems(@RequestParam("ids") List<Long> ids);
}