package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    void shouldMapAuthenticationToResponse() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "john", "john@example.com", "encrypted", now);
        Authentication auth = new Authentication(user, "jwt-token");

        LoginResponse response = LoginResponse.from(auth);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

}
