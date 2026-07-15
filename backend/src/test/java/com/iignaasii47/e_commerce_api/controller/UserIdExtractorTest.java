package com.iignaasii47.e_commerce_api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdExtractorTest {

    private static final String SECRET = "myTestSecretKeyThatIsLongEnoughForHS256Algorithm123";

    private final UserIdExtractor extractor = new UserIdExtractor(SECRET);

    private final com.iignaasii47.e_commerce_api.infrastructure.security.JwtTokenProvider tokenProvider =
            new com.iignaasii47.e_commerce_api.infrastructure.security.JwtTokenProvider(SECRET, 3600000L);

    @Test
    void shouldExtractUserIdFromValidToken() {
        String token = tokenProvider.generateToken(42L, "john");
        String authHeader = "Bearer " + token;

        Long userId = extractor.extract(authHeader);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldThrowWhenHeaderIsNull() {
        assertThatThrownBy(() -> extractor.extract(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Authorization header");
    }

    @Test
    void shouldThrowWhenHeaderDoesNotStartWithBearer() {
        assertThatThrownBy(() -> extractor.extract("Basic abc123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Authorization header");
    }

    @Test
    void shouldThrowWhenHeaderIsEmpty() {
        assertThatThrownBy(() -> extractor.extract(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Authorization header");
    }

    @Test
    void shouldThrowWhenTokenIsMalformed() {
        assertThatThrownBy(() -> extractor.extract("Bearer invalid.token"))
                .isInstanceOf(io.jsonwebtoken.JwtException.class);
    }

    @Test
    void shouldExtractDifferentUserIds() {
        String token1 = tokenProvider.generateToken(10L, "alice");
        String token2 = tokenProvider.generateToken(20L, "bob");

        Long userId1 = extractor.extract("Bearer " + token1);
        Long userId2 = extractor.extract("Bearer " + token2);

        assertThat(userId1).isEqualTo(10L);
        assertThat(userId2).isEqualTo(20L);
    }

}
