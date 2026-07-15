package com.iignaasii47.e_commerce_api.infrastructure.persistence.mapper;

import com.iignaasii47.e_commerce_api.domain.model.CartItem;
import com.iignaasii47.e_commerce_api.infrastructure.persistence.entity.CartItemEntity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemMapperTest {

    @Test
    void shouldMapEntityToDomain() {
        CartItemEntity entity = new CartItemEntity(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        CartItem item = CartItemMapper.toDomain(entity);

        assertThat(item).extracting(CartItem::getId, CartItem::getUserId, CartItem::getProductId,
                        CartItem::getProductName, CartItem::getQuantity)
                .containsExactly(1L, 10L, 5L, "Keyboard", 2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("149.99");
    }

    @Test
    void shouldMapDomainToEntity() {
        CartItem item = new CartItem(1L, 10L, 5L, "Keyboard", new BigDecimal("149.99"), 2);

        CartItemEntity entity = CartItemMapper.toEntity(item);

        assertThat(entity).extracting(CartItemEntity::getId, CartItemEntity::getUserId,
                        CartItemEntity::getProductId, CartItemEntity::getProductName,
                        CartItemEntity::getQuantity)
                .containsExactly(1L, 10L, 5L, "Keyboard", 2);
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("149.99");
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(CartItemMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDomainIsNull() {
        assertThat(CartItemMapper.toEntity(null)).isNull();
    }

}
