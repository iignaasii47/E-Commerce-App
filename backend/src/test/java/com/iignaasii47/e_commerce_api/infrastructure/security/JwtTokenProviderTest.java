package com.iignaasii47.e_commerce_api.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private static final String SECRET = "myTestSecretKeyThatIsLongEnoughForHS256Algorithm123";

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 3600000L);

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

    @Test
    void shouldGenerateTokenWithCorrectSubject() {
        String token = tokenProvider.generateToken(42L, "alice");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("alice");
    }

    @Test
    void shouldGenerateTokenWithUserIdClaim() {
        String token = tokenProvider.generateToken(42L, "alice");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("userId", Long.class)).isEqualTo(42L);
    }

    @Test
    void shouldGenerateTokenWithExpirationInTheFuture() {
        String token = tokenProvider.generateToken(1L, "john");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getExpiration())
                .isAfter(claims.getIssuedAt());
    }

    @Test
    void shouldValidateAndReturnUserIdForValidToken() {
        String token = tokenProvider.generateToken(42L, "alice");

        Long userId = tokenProvider.validateAndGetUserId(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        assertThatThrownBy(() -> tokenProvider.validateAndGetUserId("invalid.token.here"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldThrowWhenTokenSignatureIsInvalid() {
        String token = tokenProvider.generateToken(1L, "john");
        String tampered = token.substring(0, token.length() - 5) + "AAAAA";

        assertThatThrownBy(() -> tokenProvider.validateAndGetUserId(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET, 1L);
        String token = shortLivedProvider.generateToken(1L, "john");

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> shortLivedProvider.validateAndGetUserId(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldGenerateTokenWithImmediateExpirationCheck() {
        String token = tokenProvider.generateToken(1L, "john");

        Long userId = tokenProvider.validateAndGetUserId(token);

        assertThat(userId).isNotNull();
    }
}
