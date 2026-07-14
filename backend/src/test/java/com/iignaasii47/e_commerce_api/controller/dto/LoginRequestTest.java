package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void shouldCreateWithConstructor() {
        LoginRequest request = new LoginRequest("john@example.com", "secret123");

        assertThat(request.getEmail()).isEqualTo("john@example.com");
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    void shouldSetAndGetEmail() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");

        assertThat(request.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldSetAndGetPassword() {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret123");

        assertThat(request.getPassword()).isEqualTo("secret123");
    }

}
