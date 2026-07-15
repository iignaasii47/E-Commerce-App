package com.iignaasii47.e_commerce_api.infrastructure.persistence.entity;

import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void shouldCreateWithConstructor() {
        UserEntity entity = TestFixtures.aJohnUserEntity();

        assertThat(entity).extracting(UserEntity::getId, UserEntity::getUsername,
                        UserEntity::getEmail, UserEntity::getPassword, UserEntity::getCreatedAt)
                .containsExactly(1L, "john", "john@example.com", "encrypted", TestFixtures.FIXED_TIME);
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        UserEntity entity = new UserEntity();
        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
    }

}
