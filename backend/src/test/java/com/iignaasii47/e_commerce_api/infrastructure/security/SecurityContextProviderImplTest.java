package com.iignaasii47.e_commerce_api.infrastructure.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityContextProviderImplTest {

    private final SecurityContextProviderImpl provider = new SecurityContextProviderImpl();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserId() {
        var authentication = new UsernamePasswordAuthenticationToken(42L, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long userId = provider.getCurrentUserId();

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldThrowWhenNoAuthentication() {
        assertThatThrownBy(provider::getCurrentUserId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No authenticated user found");
    }
}
