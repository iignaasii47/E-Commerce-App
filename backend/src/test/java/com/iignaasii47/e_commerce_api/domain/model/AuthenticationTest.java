package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldStoreUserAndTokens() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);
        Authentication auth = new Authentication(user, "access-jwt", "refresh-jwt");

        assertThat(auth.getUser()).isEqualTo(user);
        assertThat(auth.getAccessToken()).isEqualTo("access-jwt");
        assertThat(auth.getRefreshToken()).isEqualTo("refresh-jwt");
    }

}
