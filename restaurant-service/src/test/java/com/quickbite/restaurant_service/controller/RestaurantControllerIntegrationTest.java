package com.quickbite.restaurant_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.restaurant_service.dto.MenuItemRequest;
import com.quickbite.restaurant_service.dto.RestaurantRequest;
import com.quickbite.restaurant_service.entity.Restaurant;
import com.quickbite.restaurant_service.repository.MenuItemRepository;
import com.quickbite.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Create Restaurant -> Add Menu Item -> Retrieve by ID")
    void fullRestaurantWorkflow() throws Exception {
        // 1. Create Restaurant as OWNER
        RestaurantRequest restReq = new RestaurantRequest(
                "Spice Villa", "Authentic Indian Flavors", "123 Spice Way", "Indian", "spice.jpg"
        );

        String createRestResponse = mockMvc.perform(post("/api/restaurants")
                        .header("X-User-Id", 101L)
                        .header("X-User-Role", "RESTAURANT_OWNER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Spice Villa"))
                .andReturn().getResponse().getContentAsString();

        Long restaurantId = objectMapper.readTree(createRestResponse).get("id").asLong();

        // 2. Add MenuItem to created Restaurant as OWNER
        MenuItemRequest itemReq = new MenuItemRequest(
                "Paneer Tikka", "Grilled cottage cheese", BigDecimal.valueOf(250.00), "Starters", "tikka.jpg"
        );

        mockMvc.perform(post("/api/restaurants/" + restaurantId + "/menu")
                        .header("X-User-Id", 101L)
                        .header("X-User-Role", "RESTAURANT_OWNER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Paneer Tikka"))
                .andExpect(jsonPath("$.price").value(250.00));

        // 3. Retrieve restaurant by ID and verify menu item is included
        mockMvc.perform(get("/api/restaurants/" + restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spice Villa"))
                .andExpect(jsonPath("$.menu").isArray())
                .andExpect(jsonPath("$.menu[0].name").value("Paneer Tikka"));

        // 4. Listing restaurants
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Spice Villa"));
    }

    @Test
    @DisplayName("Integration: Create Restaurant rejected for CUSTOMER role (403 Forbidden)")
    void createRestaurant_asCustomer_forbidden() throws Exception {
        RestaurantRequest restReq = new RestaurantRequest(
                "Customer Cafe", "Desc", "Address", "Cafe", null
        );

        mockMvc.perform(post("/api/restaurants")
                        .header("X-User-Id", 55L)
                        .header("X-User-Role", "CUSTOMER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restReq)))
                .andExpect(status().isForbidden());
    }
}
