package com.quickbite.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.payment_service.entity.Payment;
import com.quickbite.payment_service.events.PaymentResultEvent;
import com.quickbite.payment_service.razorpay.RazorpayClient;
import com.quickbite.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RazorpayClient razorpayClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentController paymentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    @DisplayName("createOrder: creates Razorpay order for PENDING payment")
    void createOrder_success() throws Exception {
        PaymentController.CreateRequest req = new PaymentController.CreateRequest(10L);

        when(razorpayClient.isConfigured()).thenReturn(true);
        when(razorpayClient.keyId()).thenReturn("rzp_test_123");

        Payment payment = new Payment(10L, 50L, BigDecimal.valueOf(450.00), Payment.PENDING);

        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));
        when(razorpayClient.createOrder(eq(45000L), eq("INR"), eq("qb_order_10"), eq(10L)))
                .thenReturn("order_rzp_12345");

        mockMvc.perform(post("/api/payments/razorpay/order")
                        .header("X-User-Id", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.razorpayOrderId").value("order_rzp_12345"))
                .andExpect(jsonPath("$.keyId").value("rzp_test_123"))
                .andExpect(jsonPath("$.amount").value(45000));

        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("createOrder: returns 403 Forbidden when accessed by non-owner")
    void createOrder_nonOwner_forbidden() throws Exception {
        PaymentController.CreateRequest req = new PaymentController.CreateRequest(10L);
        when(razorpayClient.isConfigured()).thenReturn(true);

        Payment payment = new Payment(10L, 50L, BigDecimal.valueOf(450.00), Payment.PENDING);
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));

        mockMvc.perform(post("/api/payments/razorpay/order")
                        .header("X-User-Id", 999L) // wrong user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("verify: marks payment COMPLETED and publishes PaymentResultEvent")
    void verify_validSignature_completesPayment() throws Exception {
        PaymentController.VerifyRequest req = new PaymentController.VerifyRequest(
                10L, "order_rzp_123", "pay_rzp_456", "valid_sig"
        );

        Payment payment = new Payment(10L, 50L, BigDecimal.valueOf(450.00), Payment.PENDING);
        payment.setRazorpayOrderId("order_rzp_123");

        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));
        when(razorpayClient.isValidSignature("order_rzp_123", "pay_rzp_456", "valid_sig")).thenReturn(true);

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("X-User-Id", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.orderId").value(10));

        assertThat(payment.getStatus()).isEqualTo(Payment.COMPLETED);
        assertThat(payment.getRazorpayPaymentId()).isEqualTo("pay_rzp_456");
        verify(paymentRepository).save(payment);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq("payment-events"), eq("10"), captor.capture());
        assertThat(captor.getValue()).isInstanceOf(PaymentResultEvent.class);
        PaymentResultEvent resultEvent = (PaymentResultEvent) captor.getValue();
        assertThat(resultEvent.orderId()).isEqualTo(10L);
        assertThat(resultEvent.success()).isTrue();
    }

    @Test
    @DisplayName("verify: invalid signature throws 400 Bad Request")
    void verify_invalidSignature_throwsBadRequest() throws Exception {
        PaymentController.VerifyRequest req = new PaymentController.VerifyRequest(
                10L, "order_rzp_123", "pay_rzp_456", "bad_sig"
        );

        Payment payment = new Payment(10L, 50L, BigDecimal.valueOf(450.00), Payment.PENDING);
        payment.setRazorpayOrderId("order_rzp_123");

        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));
        when(razorpayClient.isValidSignature("order_rzp_123", "pay_rzp_456", "bad_sig")).thenReturn(false);

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("X-User-Id", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("verify: idempotent when payment is already COMPLETED")
    void verify_alreadyCompleted_idempotent() throws Exception {
        PaymentController.VerifyRequest req = new PaymentController.VerifyRequest(
                10L, "order_rzp_123", "pay_rzp_456", "sig"
        );

        Payment payment = new Payment(10L, 50L, BigDecimal.valueOf(450.00), Payment.COMPLETED);
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));

        mockMvc.perform(post("/api/payments/razorpay/verify")
                        .header("X-User-Id", 50L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}
