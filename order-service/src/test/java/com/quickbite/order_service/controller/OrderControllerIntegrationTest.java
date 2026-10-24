package com.quickbite.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.order_service.client.MenuClient;
import com.quickbite.order_service.client.MenuItemDto;
import com.quickbite.order_service.dto.OrderItemRequest;
import com.quickbite.order_service.dto.PlaceOrderRequest;
import com.quickbite.order_service.entity.Order;
import com.quickbite.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private MenuClient menuClient;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Place Order -> Retrieve by ID -> List my orders")
    void fullOrderLifecycle() throws Exception {
        // Mock MenuClient to return available menu items
        MenuItemDto item1 = new MenuItemDto(10L, "Pepperoni Pizza", BigDecimal.valueOf(350.00), true);
        when(menuClient.getItems(List.of(10L))).thenReturn(List.of(item1));

        PlaceOrderRequest req = new PlaceOrderRequest(1L, "Flat 402, Sunshine Apts", List.of(new OrderItemRequest(10L, 2)));

        // 1. Place order as Customer 55
        String responseStr = mockMvc.perform(post("/api/orders")
                        .header("X-User-Id", 55L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.totalAmount").value(700.00))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(responseStr).get("id").asLong();

        // Verify Kafka event published
        verify(kafkaTemplate).send(eq("order-events"), eq(orderId.toString()), any());

        // 2. Fetch order by ID
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("X-User-Id", 55L)
                        .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items[0].itemName").value("Pepperoni Pizza"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        // 3. List my orders
        mockMvc.perform(get("/api/orders/my")
                        .header("X-User-Id", 55L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(orderId));

        // 4. Unauthorized other customer cannot fetch order
        mockMvc.perform(get("/api/orders/" + orderId)
                        .header("X-User-Id", 99L)
                        .header("X-User-Role", "CUSTOMER"))
                .andExpect(status().isForbidden());
    }
}
