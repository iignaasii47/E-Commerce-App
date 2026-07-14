package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, Month.JANUARY, 1, 12, 0);

    @Test
    void shouldCreateWithConstructor() {
        UserEntity entity = new UserEntity(1L, "john", "john@example.com", "encrypted", FIXED_TIME);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUsername()).isEqualTo("john");
        assertThat(entity.getEmail()).isEqualTo("john@example.com");
        assertThat(entity.getPassword()).isEqualTo("encrypted");
        assertThat(entity.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldSetAndGetId() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);

        assertThat(entity.getId()).isEqualTo(1L);
    }

    @Test
    void shouldSetAndGetUsername() {
        UserEntity entity = new UserEntity();
        entity.setUsername("john");

        assertThat(entity.getUsername()).isEqualTo("john");
    }

    @Test
    void shouldSetAndGetEmail() {
        UserEntity entity = new UserEntity();
        entity.setEmail("john@example.com");

        assertThat(entity.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void shouldSetAndGetPassword() {
        UserEntity entity = new UserEntity();
        entity.setPassword("encrypted");

        assertThat(entity.getPassword()).isEqualTo("encrypted");
    }

    @Test
    void shouldSetAndGetCreatedAt() {
        UserEntity entity = new UserEntity();
        entity.setCreatedAt(FIXED_TIME);

        assertThat(entity.getCreatedAt()).isEqualTo(FIXED_TIME);
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        UserEntity entity = new UserEntity();
        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
    }

}
