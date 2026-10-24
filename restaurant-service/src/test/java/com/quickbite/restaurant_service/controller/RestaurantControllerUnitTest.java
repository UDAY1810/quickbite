package com.quickbite.restaurant_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.restaurant_service.dto.*;
import com.quickbite.restaurant_service.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private RestaurantService service;

    @InjectMocks
    private RestaurantController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/restaurants returns restaurant list")
    void list_returnsList() throws Exception {
        RestaurantResponse r = new RestaurantResponse(1L, "Test Bistro", "Desc", "123 Main St", "Italian", BigDecimal.valueOf(4.5), true, "img.jpg", List.of());
        when(service.list(null)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Bistro"));

        verify(service).list(null);
    }

    @Test
    @DisplayName("GET /api/restaurants/{id} returns single restaurant")
    void get_returnsRestaurant() throws Exception {
        RestaurantResponse r = new RestaurantResponse(1L, "Test Bistro", "Desc", "123 Main St", "Italian", BigDecimal.valueOf(4.5), true, "img.jpg", List.of());
        when(service.get(1L)).thenReturn(r);

        mockMvc.perform(get("/api/restaurants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Bistro"));
    }

    @Test
    @DisplayName("POST /api/restaurants creates restaurant with 201 Created")
    void create_createsRestaurant() throws Exception {
        RestaurantRequest req = new RestaurantRequest("New Diner", "Great food", "Downtown", "American", "diner.png");
        RestaurantResponse resp = new RestaurantResponse(10L, "New Diner", "Great food", "Downtown", "American", BigDecimal.ZERO, true, "diner.png", List.of());

        when(service.create(any(RestaurantRequest.class), eq(5L), eq("RESTAURANT_OWNER"))).thenReturn(resp);

        mockMvc.perform(post("/api/restaurants")
                        .header("X-User-Id", 5L)
                        .header("X-User-Role", "RESTAURANT_OWNER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Diner"));
    }

    @Test
    @DisplayName("GET /api/restaurants/internal/menu-items returns items by ids")
    void internalItems_returnsDtos() throws Exception {
        MenuItemInternalDto dto = new MenuItemInternalDto(100L, "Fries", BigDecimal.valueOf(99.0), true);
        when(service.internalItems(List.of(100L))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/restaurants/internal/menu-items?ids=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fries"));
    }
}
