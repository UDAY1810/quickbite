package com.quickbite.order_service.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleConflict: returns 409 status and message")
    void handleConflict_returnsConflict() {
        ConflictException ex = new ConflictException("Order already completed");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleConflict(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(res.getBody()).containsEntry("message", "Order already completed");
    }

    @Test
    @DisplayName("handleUnauthorized: returns 401 status and message")
    void handleUnauthorized_returnsUnauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Missing authentication");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleUnauthorized(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).containsEntry("message", "Missing authentication");
    }

    @Test
    @DisplayName("handleForbidden: returns 403 status and message")
    void handleForbidden_returnsForbidden() {
        ForbiddenException ex = new ForbiddenException("Cannot view another customer's order");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleForbidden(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).containsEntry("message", "Cannot view another customer's order");
    }

    @Test
    @DisplayName("handleBadRequest: returns 400 status and message")
    void handleBadRequest_returnsBadRequest() {
        BadRequestException ex = new BadRequestException("Order item quantity must be positive");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleBadRequest(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsEntry("message", "Order item quantity must be positive");
    }

    @Test
    @DisplayName("handleUnavailable: returns 503 status and message")
    void handleUnavailable_returnsServiceUnavailable() {
        ServiceUnavailableException ex = new ServiceUnavailableException("Restaurant service is currently unreachable");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleUnavailable(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(res.getBody()).containsEntry("message", "Restaurant service is currently unreachable");
    }

    @Test
    @DisplayName("handleNotFound: returns 404 status and message")
    void handleNotFound_returnsNotFound() {
        NotFoundException ex = new NotFoundException("Order #55 not found");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleNotFound(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).containsEntry("message", "Order #55 not found");
    }

    @Test
    @DisplayName("handleValidation: returns 400 Bad Request with field mapping")
    void handleValidation_returnsFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createOrderRequest");
        bindingResult.addError(new FieldError("createOrderRequest", "restaurantId", "must not be null"));
        bindingResult.addError(new FieldError("createOrderRequest", "items", "must not be empty"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerUnitTest.class.getDeclaredMethod("sampleMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> res = exceptionHandler.handleValidation(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsEntry("restaurantId", "must not be null");
        assertThat(res.getBody()).containsEntry("items", "must not be empty");
    }

    private void sampleMethod(String param) {}
}
