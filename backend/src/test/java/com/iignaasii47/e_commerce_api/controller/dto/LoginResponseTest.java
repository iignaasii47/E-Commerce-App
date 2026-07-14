package com.iignaasii47.e_commerce_api.controller.dto;

import com.iignaasii47.e_commerce_api.domain.model.Authentication;
import com.iignaasii47.e_commerce_api.domain.model.User;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldMapAuthenticationToResponse() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        Authentication auth = new Authentication(user, "jwt-token");

        LoginResponse response = LoginResponse.from(auth);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(FIXED_TIME);
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

}
