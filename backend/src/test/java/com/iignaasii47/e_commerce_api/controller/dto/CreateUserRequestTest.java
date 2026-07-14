package com.iignaasii47.e_commerce_api.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserRequestTest {

    @Test
    void shouldCreateWithConstructor() {
        CreateUserRequest request = new CreateUserRequest("john", "john@example.com", "secret123");

        assertThat(request.getUsername()).isEqualTo("john");
        assertThat(request.getEmail()).isEqualTo("john@example.com");
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    void shouldSetAndGetUsername() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");

        assertThat(request.getUsername()).isEqualTo("john");
    }

    @Test
    void shouldSetAndGetEmail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("john@example.com");

        assertThat(request.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldSetAndGetPassword() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPassword("secret123");

        assertThat(request.getPassword()).isEqualTo("secret123");
    }

}
