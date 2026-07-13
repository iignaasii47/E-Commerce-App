package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldMapEntityToDomain() {
        UserEntity entity = new UserEntity(1L, "john", "john@example.com", "encrypted", FIXED_TIME);

        User user = UserMapper.toDomain(entity);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("john");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("encrypted");
        assertThat(user.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldMapDomainToEntity() {
        User user = new User(1L, "john", "john@example.com", "encrypted", FIXED_TIME);

        UserEntity entity = UserMapper.toEntity(user);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUsername()).isEqualTo("john");
        assertThat(entity.getEmail()).isEqualTo("john@example.com");
        assertThat(entity.getPassword()).isEqualTo("encrypted");
        assertThat(entity.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(UserMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(UserMapper.toEntity(null)).isNull();
    }

    @Test
    void shouldHandleNullId() {
        User user = new User(null, "john", "john@example.com", "pass", null);
        UserEntity entity = UserMapper.toEntity(user);

        assertThat(entity.getId()).isNull();
    }

}
