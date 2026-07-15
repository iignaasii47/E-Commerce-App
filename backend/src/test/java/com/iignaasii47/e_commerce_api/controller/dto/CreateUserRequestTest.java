package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    @Test
    void shouldCreateWithConstructor() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com", "secret123");

        assertThat(request.username()).isEqualTo("john");
        assertThat(request.email()).isEqualTo("john@example.com");
        assertThat(request.password()).isEqualTo("secret123");
    }

}
