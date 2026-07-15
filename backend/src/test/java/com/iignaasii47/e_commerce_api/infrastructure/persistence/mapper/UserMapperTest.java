package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;
import com.iignaasii47.e_commerce_api.util.TestFixtures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        UserEntity entity = TestFixtures.aJohnUserEntity();

        User user = UserMapper.toDomain(entity);

        assertThat(user).extracting(User::getId, User::getUsername, User::getEmail,
                        User::getPassword, User::getCreatedAt)
                .containsExactly(entity.getId(), entity.getUsername(), entity.getEmail(),
                        entity.getPassword(), entity.getCreatedAt());
    }

    @Test
    void shouldMapDomainToEntity() {
        User user = TestFixtures.aJohnUser();

        UserEntity entity = UserMapper.toEntity(user);

        assertThat(entity).extracting(UserEntity::getId, UserEntity::getUsername,
                        UserEntity::getEmail, UserEntity::getPassword, UserEntity::getCreatedAt)
                .containsExactly(user.getId(), user.getUsername(), user.getEmail(),
                        user.getPassword(), user.getCreatedAt());
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
