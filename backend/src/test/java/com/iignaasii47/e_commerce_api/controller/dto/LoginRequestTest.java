package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void shouldCreateWithConstructor() {
        LoginRequest request = new LoginRequest("john@example.com", "secret123");

        assertThat(request.email()).isEqualTo("john@example.com");
        assertThat(request.password()).isEqualTo("secret123");
    }

}
