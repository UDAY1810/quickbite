package com.quickbite.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.order_service.dto.OrderItemRequest;
import com.quickbite.order_service.dto.OrderResponse;
import com.quickbite.order_service.dto.PlaceOrderRequest;
import com.quickbite.order_service.service.OrderService;
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
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("POST /api/orders places order and returns 201 Created")
    void placeOrder_returnsCreated() throws Exception {
        PlaceOrderRequest req = new PlaceOrderRequest(1L, "123 Main St", List.of(new OrderItemRequest(10L, 2)));
        OrderResponse resp = new OrderResponse(100L, 42L, 1L, "PENDING", BigDecimal.valueOf(300.00), "123 Main St", Instant.now(), List.of());

        when(orderService.placeOrder(any(PlaceOrderRequest.class), eq(42L))).thenReturn(resp);

        mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", 42L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(300.00));

        verify(orderService).placeOrder(any(PlaceOrderRequest.class), eq(42L));
    }

    @Test
    @DisplayName("GET /api/orders/my returns user orders")
    void myOrders_returnsList() throws Exception {
        OrderResponse resp = new OrderResponse(100L, 42L, 1L, "PENDING", BigDecimal.valueOf(300.00), "123 Main St", Instant.now(), List.of());
        when(orderService.myOrders(42L)).thenReturn(List.of(resp));

        mockMvc.perform(get("/api/orders/my")
                        .header("X-User-Id", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));

        verify(orderService).myOrders(42L);
    }

    @Test
    @DisplayName("GET /api/orders/{id} returns single order")
    void getOrder_returnsOrder() throws Exception {
        OrderResponse resp = new OrderResponse(100L, 42L, 1L, "PENDING", BigDecimal.valueOf(300.00), "123 Main St", Instant.now(), List.of());
        when(orderService.get(100L, 42L, "CUSTOMER")).thenReturn(resp);

        mockMvc.perform(get("/api/orders/100")
                        .header("X-User-Id", 42L)
                        .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));

        verify(orderService).get(100L, 42L, "CUSTOMER");
    }
}
