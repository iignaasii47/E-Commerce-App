package com.iignaasii47.e_commerce_api.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldDetectExpiredToken() {
        LocalDateTime pastExpiry = FIXED_TIME.minusDays(1);
        RefreshToken token = new RefreshToken(1L, "t", 10L, pastExpiry, false, FIXED_TIME);

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void shouldDetectNonExpiredToken() {
        LocalDateTime futureExpiry = FIXED_TIME.plusYears(100);
        RefreshToken token = new RefreshToken(1L, "t", 10L, futureExpiry, false, FIXED_TIME);

        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void shouldDetectRevokedToken() {
        RefreshToken token = new RefreshToken(1L, "t", 10L,
                FIXED_TIME.plusYears(100), true, FIXED_TIME);

        assertThat(token.isRevoked()).isTrue();
    }

    @Test
    void shouldDetectNonRevokedToken() {
        RefreshToken token = new RefreshToken(1L, "t", 10L,
                FIXED_TIME.plusYears(100), false, FIXED_TIME);

        assertThat(token.isRevoked()).isFalse();
    }

    @Test
    void shouldBeValidWhenNotExpiredAndNotRevoked() {
        RefreshToken token = new RefreshToken(1L, "t", 10L,
                FIXED_TIME.plusYears(100), false, FIXED_TIME);

        assertThat(token.isValid()).isTrue();
    }

    @Test
    void shouldBeInvalidWhenExpired() {
        LocalDateTime pastExpiry = FIXED_TIME.minusDays(1);
        RefreshToken token = new RefreshToken(1L, "t", 10L, pastExpiry, false, FIXED_TIME);

        assertThat(token.isValid()).isFalse();
    }

    @Test
    void shouldBeInvalidWhenRevoked() {
        RefreshToken token = new RefreshToken(1L, "t", 10L,
                FIXED_TIME.plusYears(100), true, FIXED_TIME);

        assertThat(token.isValid()).isFalse();
    }

    @Test
    void shouldReturnConstructorValues() {
        LocalDateTime expiry = FIXED_TIME.plusDays(30);
        RefreshToken token = new RefreshToken(42L, "refresh-token", 7L, expiry, false, FIXED_TIME);

        assertThat(token.getId()).isEqualTo(42L);
        assertThat(token.getToken()).isEqualTo("refresh-token");
        assertThat(token.getUserId()).isEqualTo(7L);
        assertThat(token.getExpiryDate()).isEqualTo(expiry);
        assertThat(token.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldReturnCorrectExpiryDate() {
        LocalDateTime twoHoursFromNow = FIXED_TIME.plusHours(2);
        RefreshToken token = new RefreshToken(1L, "t", 10L, twoHoursFromNow, false, FIXED_TIME);

        assertThat(token.getExpiryDate()).isEqualTo(twoHoursFromNow);
    }
}
