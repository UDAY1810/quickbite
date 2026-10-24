package com.quickbite.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.PaymentResultEvent;
import com.quickbite.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration: Create Razorpay Order -> Verify Signature -> Status COMPLETED")
    void fullPaymentWorkflow() throws Exception {
        // Pre-seed a pending payment as if OrderPlacedEvent arrived
        Payment payment = new Payment(101L, 42L, BigDecimal.valueOf(499.00), Payment.PENDING);
        paymentRepository.save(payment);

        // 1. Create order
        PaymentController.CreateRequest createReq = new PaymentController.CreateRequest(101L);
        String createResp = mockMvc.perform(post("/api/payments/razorpay/order")
                        .header("X-User-Id", 42L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(101))
                .andExpect(jsonPath("$.amount").value(49900))
                .andReturn().getResponse().getContentAsString();

        String razorpayOrderId = objectMapper.readTree(createResp).get("razorpayOrderId").asText();
        assertThat(razorpayOrderId).isNotEmpty();

        // 2. Verify payment (in test/demo mode, simulated signatures like 'simulated_' or 'sig_' are accepted)
        PaymentController.VerifyRequest verifyReq = new PaymentController.VerifyRequest(
                101L, razorpayOrderId, "pay_rzp_test_1", "simulated_valid_signature"
        );

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("X-User-Id", 42L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.orderId").value(101));

        // 3. Confirm payment in DB is COMPLETED
        Payment updated = paymentRepository.findByOrderId(101L).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(Payment.COMPLETED);
        assertThat(updated.getRazorpayPaymentId()).isEqualTo("pay_rzp_test_1");

        // 4. Confirm PaymentResultEvent published to Kafka
        verify(kafkaTemplate).send(eq("payment-events"), eq("101"), any(PaymentResultEvent.class));
    }
}
