package com.quickbite.restaurant_service.exception;

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
        ConflictException ex = new ConflictException("Restaurant with name already exists");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleConflict(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(res.getBody()).containsEntry("message", "Restaurant with name already exists");
    }

    @Test
    @DisplayName("handleUnauthorized: returns 401 status and message")
    void handleUnauthorized_returnsUnauthorized() {
        UnauthorizedException ex = new UnauthorizedException("Invalid token");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleUnauthorized(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(res.getBody()).containsEntry("message", "Invalid token");
    }

    @Test
    @DisplayName("handleForbidden: returns 403 status and message")
    void handleForbidden_returnsForbidden() {
        ForbiddenException ex = new ForbiddenException("Only owner can modify menu");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleForbidden(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).containsEntry("message", "Only owner can modify menu");
    }

    @Test
    @DisplayName("handleNotFound: returns 404 status and message")
    void handleNotFound_returnsNotFound() {
        NotFoundException ex = new NotFoundException("Restaurant not found");
        ResponseEntity<Map<String, String>> res = exceptionHandler.handleNotFound(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).containsEntry("message", "Restaurant not found");
    }

    @Test
    @DisplayName("handleValidation: returns 400 Bad Request with field errors")
    void handleValidation_returnsBadRequest() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "createRestaurantRequest");
        bindingResult.addError(new FieldError("createRestaurantRequest", "name", "must not be blank"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerUnitTest.class.getDeclaredMethod("sampleMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> res = exceptionHandler.handleValidation(ex);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).containsEntry("name", "must not be blank");
    }

    private void sampleMethod(String param) {}
}
