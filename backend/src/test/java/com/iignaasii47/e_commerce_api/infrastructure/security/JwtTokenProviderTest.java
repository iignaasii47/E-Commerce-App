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

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 900000L, 604800000L);

    @Test
    void shouldGenerateAccessToken() {
        String token = tokenProvider.generateAccessToken(1L, "john");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void shouldGenerateRefreshToken() {
        String token = tokenProvider.generateRefreshToken(1L, "john");

        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void shouldGenerateUniqueTokensForDifferentUsers() {
        String token1 = tokenProvider.generateAccessToken(1L, "john");
        String token2 = tokenProvider.generateAccessToken(2L, "jane");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldGenerateTokenWithCorrectSubject() {
        String token = tokenProvider.generateAccessToken(42L, "alice");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("alice");
    }

    @Test
    void shouldGenerateAccessTokenWithTypeClaim() {
        String token = tokenProvider.generateAccessToken(42L, "alice");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("type", String.class)).isEqualTo("access");
    }

    @Test
    void shouldGenerateRefreshTokenWithTypeClaim() {
        String token = tokenProvider.generateRefreshToken(42L, "alice");

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("type", String.class)).isEqualTo("refresh");
    }

    @Test
    void shouldGenerateTokenWithUserIdClaim() {
        String token = tokenProvider.generateAccessToken(42L, "alice");

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
        String token = tokenProvider.generateAccessToken(1L, "john");

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
    void shouldValidateAccessTokenAndReturnUserId() {
        String token = tokenProvider.generateAccessToken(42L, "alice");

        Long userId = tokenProvider.validateAccessTokenAndGetUserId(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldValidateRefreshTokenAndReturnUserId() {
        String token = tokenProvider.generateRefreshToken(42L, "alice");

        Long userId = tokenProvider.validateRefreshTokenAndGetUserId(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void shouldRejectRefreshTokenAsAccessToken() {
        String token = tokenProvider.generateRefreshToken(42L, "alice");

        assertThatThrownBy(() -> tokenProvider.validateAccessTokenAndGetUserId(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token is not an access token");
    }

    @Test
    void shouldRejectAccessTokenAsRefreshToken() {
        String token = tokenProvider.generateAccessToken(42L, "alice");

        assertThatThrownBy(() -> tokenProvider.validateRefreshTokenAndGetUserId(token))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token is not a refresh token");
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        assertThatThrownBy(() -> tokenProvider.validateAccessTokenAndGetUserId("invalid.token.here"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldThrowWhenTokenSignatureIsInvalid() {
        String token = tokenProvider.generateAccessToken(1L, "john");
        String tampered = token.substring(0, token.length() - 5) + "AAAAA";

        assertThatThrownBy(() -> tokenProvider.validateAccessTokenAndGetUserId(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldThrowWhenTokenIsExpired() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(SECRET, 1L, 1L);
        String token = shortLivedProvider.generateAccessToken(1L, "john");

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> shortLivedProvider.validateAccessTokenAndGetUserId(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void shouldGenerateTokenWithImmediateExpirationCheck() {
        String token = tokenProvider.generateAccessToken(1L, "john");

        Long userId = tokenProvider.validateAccessTokenAndGetUserId(token);

        assertThat(userId).isNotNull();
    }

}
