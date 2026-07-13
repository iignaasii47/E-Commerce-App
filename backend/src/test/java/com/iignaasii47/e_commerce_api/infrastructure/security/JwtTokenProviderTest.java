package com.iignaasii47.e_commerce_api.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(
            "myTestSecretKeyThatIsLongEnoughForHS256Algorithm123",
            3600000L
    );

    @Test
    void shouldGenerateToken() {
        String token = tokenProvider.generateToken(1L, "john");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void shouldGenerateUniqueTokensForDifferentUsers() {
        String token1 = tokenProvider.generateToken(1L, "john");
        String token2 = tokenProvider.generateToken(2L, "jane");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldGenerateTokenWithExpiration() {
        String token = tokenProvider.generateToken(1L, "john");

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

}
