package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.exception.AiServiceException;
import com.iignaasii47.e_commerce_api.domain.exception.CartItemNotFoundException;
import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;
import com.iignaasii47.e_commerce_api.domain.exception.ProductNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn409ForDuplicateUser() {
        DuplicateUserException exception = new DuplicateUserException("user exists");

        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateUser(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 409)
                .containsEntry("error", "Conflict")
                .containsEntry("message", "user exists")
                .containsKey("timestamp");
    }

    @Test
    void shouldReturn401ForInvalidCredentials() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid username or password");

        ResponseEntity<Map<String, Object>> response = handler.handleInvalidCredentials(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 401)
                .containsEntry("error", "Unauthorized")
                .containsEntry("message", "Invalid username or password")
                .containsKey("timestamp");
    }

    @Test
    void shouldReturn502ForAiServiceException() {
        AiServiceException exception = new AiServiceException("AI service error");

        ResponseEntity<Map<String, Object>> response = handler.handleAiService(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 502)
                .containsEntry("error", "Bad Gateway")
                .containsEntry("message", "AI service error")
                .containsKey("timestamp");
    }

    @Test
    void shouldReturn404ForProductNotFound() {
        ProductNotFoundException exception = new ProductNotFoundException("Product not found with id: 99");

        ResponseEntity<Map<String, Object>> response = handler.handleProductNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 404)
                .containsEntry("error", "Not Found")
                .containsEntry("message", "Product not found with id: 99")
                .containsKey("timestamp");
    }

    @Test
    void shouldReturn404ForCartItemNotFound() {
        CartItemNotFoundException exception = new CartItemNotFoundException("Cart item not found with id: 5");

        ResponseEntity<Map<String, Object>> response = handler.handleCartItemNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 404)
                .containsEntry("error", "Not Found")
                .containsEntry("message", "Cart item not found with id: 5")
                .containsKey("timestamp");
    }

    @Test
    void shouldReturn500ForGenericException() {
        Exception exception = new Exception("unexpected");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull()
                .containsEntry("status", 500)
                .containsEntry("error", "Internal Server Error")
                .containsEntry("message", "An unexpected error occurred");
    }

}
