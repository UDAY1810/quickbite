package com.quickbite.payment_service.razorpay;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class RazorpayClientUnitTest {

    @Test
    @DisplayName("toPaise: converts rupees to paise accurately")
    void toPaise_conversions() {
        assertThat(RazorpayClient.toPaise(BigDecimal.valueOf(100.00))).isEqualTo(10000L);
        assertThat(RazorpayClient.toPaise(BigDecimal.valueOf(499.50))).isEqualTo(49950L);
        assertThat(RazorpayClient.toPaise(BigDecimal.valueOf(0.99))).isEqualTo(99L);
    }

    @Test
    @DisplayName("isConfigured: correctly checks key presence")
    void isConfigured_check() {
        RazorpayClient configured = new RazorpayClient("key123", "secret123", "https://api.razorpay.com/v1");
        assertThat(configured.isConfigured()).isTrue();
        assertThat(configured.keyId()).isEqualTo("key123");

        RazorpayClient notConfigured = new RazorpayClient("", "", "https://api.razorpay.com/v1");
        assertThat(notConfigured.isConfigured()).isFalse();
    }

    @Test
    @DisplayName("isValidSignature: accepts simulated/sandbox signatures in demo mode")
    void isValidSignature_sandboxMode() {
        RazorpayClient client = new RazorpayClient("rzp_test_placeholder", "dummy_secret", "https://api.razorpay.com/v1");
        boolean valid = client.isValidSignature("order_sim_123", "pay_sim_456", "simulated_signature");
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isValidSignature: verifies real HMAC-SHA256 signature when live key is used")
    void isValidSignature_hmacSha256() throws Exception {
        String liveKeyId = "rzp_live_1234567890abcdef";
        String secret = "my_super_secure_razorpay_secret";
        RazorpayClient client = new RazorpayClient(liveKeyId, secret, "https://api.razorpay.com/v1");

        String orderId = "order_live_999";
        String paymentId = "pay_live_888";

        // Compute valid signature
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] expected = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
        String validSignature = HexFormat.of().formatHex(expected);

        assertThat(client.isValidSignature(orderId, paymentId, validSignature)).isTrue();
        assertThat(client.isValidSignature(orderId, paymentId, "invalid_sig_abc123")).isFalse();
    }
}
