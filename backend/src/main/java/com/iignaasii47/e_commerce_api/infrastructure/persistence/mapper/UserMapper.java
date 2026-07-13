package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.User;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

public class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getPassword(), entity.getCreatedAt());
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserEntity(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getCreatedAt());
    }

}
