package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;

public class CartItemMapper {

    private CartItemMapper() {
    }

    public static CartItem toDomain(CartItemEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CartItem(
                entity.getId(),
                entity.getUserId(),
                entity.getProductId(),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity()
        );
    }

    public static CartItemEntity toEntity(CartItem item) {
        if (item == null) {
            return null;
        }
        return new CartItemEntity(
                item.getId(),
                item.getUserId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity()
        );
    }

}
