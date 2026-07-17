package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.RefreshToken;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.RefreshTokenEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenMapperTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldMapDomainToEntity() {
        RefreshToken domain = new RefreshToken(1L, "token-value", 10L,
                FIXED_TIME.plusDays(7), false, FIXED_TIME);

        RefreshTokenEntity entity = RefreshTokenMapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getToken()).isEqualTo("token-value");
        assertThat(entity.getUser()).isNotNull();
        assertThat(entity.getUser().getId()).isEqualTo(10L);
        assertThat(entity.getExpiryDate()).isEqualTo(FIXED_TIME.plusDays(7));
        assertThat(entity.isRevoked()).isEqualTo(false);
        assertThat(entity.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldMapEntityToDomain() {
        UserEntity userRef = new UserEntity();
        userRef.setId(10L);
        RefreshTokenEntity entity = new RefreshTokenEntity(2L, "entity-token", userRef,
                FIXED_TIME.plusDays(14), true, FIXED_TIME);

        RefreshToken domain = RefreshTokenMapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getToken()).isEqualTo("entity-token");
        assertThat(domain.getUserId()).isEqualTo(10L);
        assertThat(domain.getExpiryDate()).isEqualTo(FIXED_TIME.plusDays(14));
        assertThat(domain.isRevoked()).isEqualTo(true);
        assertThat(domain.getCreatedAt()).isEqualTo(FIXED_TIME);
    }
}
