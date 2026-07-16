package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.RefreshToken;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.RefreshTokenEntity;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.UserEntity;

public final class RefreshTokenMapper {

    private RefreshTokenMapper() {
    }

    public static RefreshTokenEntity toEntity(RefreshToken domain) {
        UserEntity userRef = new UserEntity();
        userRef.setId(domain.getUserId());
        return new RefreshTokenEntity(
                domain.getId(),
                domain.getToken(),
                userRef,
                domain.getExpiryDate(),
                domain.isRevokedRaw(),
                domain.getCreatedAt()
        );
    }

    public static RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getToken(),
                entity.getUser().getId(),
                entity.getExpiryDate(),
                entity.isRevoked(),
                entity.getCreatedAt()
        );
    }

}
