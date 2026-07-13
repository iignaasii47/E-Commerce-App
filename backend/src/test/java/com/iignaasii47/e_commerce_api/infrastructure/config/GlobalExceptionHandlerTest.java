package com.iignaasii47.e_commerce_api.infrastructure.config;

import com.iignaasii47.e_commerce_api.domain.exception.DuplicateUserException;
import com.iignaasii47.e_commerce_api.domain.exception.InvalidCredentialsException;

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
