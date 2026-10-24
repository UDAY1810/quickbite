package com.quickbite.user_service.exception;

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
    @DisplayName("handleConflict: returns 409 status and error message in body")
    void handleConflict_returnsConflictResponse() {
        ConflictException ex = new ConflictException("Email already registered");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleConflict(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Email already registered");
    }

    @Test
    @DisplayName("handleUnauthorized: returns 401 status and error message in body")
    void handleUnauthorized_returnsUnauthorizedResponse() {
        UnauthorizedException ex = new UnauthorizedException("Bad credentials");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleUnauthorized(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Bad credentials");
    }

    @Test
    @DisplayName("handleValidation: returns 400 Bad Request with field-level errors")
    void handleValidation_returnsBadRequestWithFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "registerRequest");
        bindingResult.addError(new FieldError("registerRequest", "email", "must be a well-formed email address"));
        bindingResult.addError(new FieldError("registerRequest", "password", "size must be between 8 and 2147483647"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerUnitTest.class.getDeclaredMethod("sampleMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("email", "must be a well-formed email address");
        assertThat(response.getBody()).containsEntry("password", "size must be between 8 and 2147483647");
    }

    private void sampleMethod(String param) {}
}
